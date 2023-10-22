package com.example.dbmsImplementation.service;

import com.example.dbmsImplementation.entity.Catalog;
import com.example.dbmsImplementation.entity.Database;
import com.example.dbmsImplementation.fileManager.DatabaseLoadingManager;
import com.example.dbmsImplementation.fileManager.DatabaseSavingManager;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatalogService {
    private Catalog catalog;
    private static String PATHNAME = "dbfiles/catalog.xml";

    public CatalogService() {
        this.catalog = DatabaseLoadingManager.loadCatalogFromFile(PATHNAME);
    }

    public void runCommand(String command) throws Exception {
        String commandLowerCase = command.strip().toLowerCase();
        if (commandLowerCase.matches("create database [a-zA-z]+")) {
            String commandSplit = command.strip().toLowerCase().split("create database")[1].strip();
            createDatabase(WordUtils.capitalize(commandSplit));
        } else if (commandLowerCase.matches("drop database [a-zA-z]+")) {
            String commandSplit = command.strip().toLowerCase().split("drop database")[1].strip();
            dropDatabase(WordUtils.capitalize(commandSplit));
        }
    }

    private void createDatabase(String databaseName) throws Exception {
        if (catalog.getDatabaseList().stream().anyMatch(database -> database.getDatabaseName().equals(databaseName))) {
            throw new Exception("Database already exists!");
        }
        Database database = new Database(databaseName);
        catalog.addDatabase(database);
        DatabaseSavingManager.saveCatalogToFile(catalog, PATHNAME);
    }

    public void dropDatabase(String databaseName) throws Exception {
        if (catalog.getDatabaseList().stream().anyMatch(database -> database.getDatabaseName().equals(databaseName))) {
            List<Database> databases = catalog.getDatabaseList().stream()
                    .filter(database -> !database.getDatabaseName().equalsIgnoreCase(databaseName))
                    .toList();
            catalog.setDatabaseList(databases);
            DatabaseSavingManager.saveCatalogToFile(catalog, PATHNAME);
        } else {
            throw new Exception("Database doesn't exists!");
        }
    }
}

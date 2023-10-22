package com.example.dbmsImplementation.service;

import com.example.dbmsImplementation.entity.Catalog;
import com.example.dbmsImplementation.entity.Database;
import com.example.dbmsImplementation.fileManager.DatabaseSavingManager;

public class CatalogService {
    private Catalog catalog;

    public void createDatabase(String databaseName) {
        Database database = new Database(databaseName);
        catalog.addDatabase(database);
    }

    public void dropDatabase(String databaseName) {

    }
}

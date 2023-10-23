package com.example.dbmsImplementation.service;

import com.example.dbmsImplementation.entity.*;
import com.example.dbmsImplementation.fileManager.DatabaseLoadingManager;
import com.example.dbmsImplementation.fileManager.DatabaseSavingManager;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CatalogService {
    private Catalog catalog;
    private static String PATHNAME = "dbfiles/catalog.xml";

    private static Database currentDatabase;

    public CatalogService() {
        this.catalog = DatabaseLoadingManager.loadCatalogFromFile(PATHNAME);
    }

    public void runCommand(String command) throws Exception {
        String commandLowerCase = command.strip().toLowerCase();
        if (commandLowerCase.matches("create database [a-zA-Z_][a-zA-Z0-9_]*")) {
            String commandSplit = command.strip().toLowerCase().split("create database")[1].strip();
            createDatabase(WordUtils.capitalize(commandSplit));
        } else if (commandLowerCase.matches("drop database [a-zA-Z_][a-zA-Z0-9_]*")) {
            String commandSplit = command.strip().toLowerCase().split("drop database")[1].strip();
            dropDatabase(WordUtils.capitalize(commandSplit));
        } else if (commandLowerCase.matches("use database [a-zA-Z_][a-zA-Z0-9_]*")) {
            String commandSplit = command.strip().toLowerCase().split("use database")[1].strip();
            useDatabase(WordUtils.capitalize(commandSplit));
        } else if (commandLowerCase.matches("drop table [a-zA-Z_][a-zA-Z0-9_]*")) {
            String commandSplit = command.strip().toLowerCase().split("drop table")[1].strip();
            dropTable(commandSplit);
        } else if (commandLowerCase.matches("create( unique)? index [a-zA-Z_][a-zA-Z0-9_]* on [a-zA-Z_][a-zA-Z0-9_]* ?(.+)")) {
            boolean isUnique = commandLowerCase.matches("create unique.*");
            String indexName = commandLowerCase.split("index")[1].strip().split("on")[0].strip() + ".ind";
            String tableName = commandLowerCase.split("index")[1].strip().split("on")[1].strip().split("[()]")[0].strip();
            List<String> attributes = List.of(commandLowerCase.split("index")[1].strip().split("on")[1].strip().split("[()]")[1].split(","));
            attributes = attributes.stream().map(String::strip).toList();
            createIndex(indexName, tableName, attributes, isUnique);
        } else if (commandLowerCase.matches("create\s+table\s*[A-Za-z_][A-Za-z0-9_]*\s*(.*)")) {
            createTableCommand(commandLowerCase);
        } else if (commandLowerCase.matches("drop index [a-zA-Z_][a-zA-Z0-9_]*")) {
            String commandSplit = command.strip().toLowerCase().split("drop index")[1].strip();
            dropIndex(commandSplit + ".ind");
        } else {
            throw new Exception("Invalid command!");
        }
    }

    private void createTableCommand(String command) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("Database is not selected!");
        }

        String tableName = "";
        List<Attribute> attributes = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();
        List<ForeignKey> foreignKeys = new ArrayList<>();
        List<String> uniqueKeys = new ArrayList<>();
        String[] statements = command.split("[,]");

        for (int index = 0; index < statements.length; index++) {
            String statement = statements[index];
            if (statement.strip().startsWith("create table")) {
                statement = statement.strip();
                tableName = statement.split("[ (]")[2];
                int indexPar = statement.indexOf("(");
                List<String> firstAttribute;
                if (statement.charAt(statement.length() - 1) == ')') {
                    firstAttribute = Stream.of(statement.substring(indexPar + 1, statement.length() - 1).split(" ")).map(String::strip).toList();
                } else {
                    firstAttribute = Stream.of(statement.substring(indexPar + 1).split(" ")).map(String::strip).toList();
                }
                if (firstAttribute.contains("unique")) {
                    uniqueKeys.add(firstAttribute.get(0));
                }
                attributes.add(createAttribute(firstAttribute));
            } else if (statement.strip().startsWith("primary key")) {
                while (statement.charAt(statement.length() - 1) != ')') {
                    index++;
                    statement += statements[index];
                }
                List<String> parts = Arrays.stream(statement.strip().split("[( )]")).toList();
                primaryKeys = parts.subList(2, parts.size());
                if (!existAttributes(attributes, primaryKeys)) {
                    throw new Exception("Invalid attributes in primary key!");
                }
            } else if (statement.strip().startsWith("foreign key")) {
                while (statement.charAt(statement.length() - 1) != ')') {
                    index++;
                    statement += statements[index];
                }
                String[] parts = statement.strip().split("(foreign key|references)");
                List<String> attributeList = Arrays.stream(parts[1].strip().split("[( )]")).toList().stream().filter(key -> !key.isEmpty()).toList();
                List<String> references = List.of(parts[2].strip().split("[( )]"));
                String refTable = references.get(0);
                if (!existTable(refTable)) {
                    throw new Exception("Invalid referenced table!");
                }
                List<String> refKeys = references.subList(1, references.size());
                foreignKeys.add(new ForeignKey(attributeList, refTable, refKeys));
                if (!existRefKeys(refTable, refKeys)) {
                    throw new Exception("Invalid referenced attributes!");
                }
            } else if (statement.strip().matches("[a-zA-Z0-9_ ()]+")) {
                List<String> attr = new ArrayList<>(List.of(statement.strip().split(" ")));
                int len = attr.size();
                String lastAttr = attr.get(len - 1);
                if (lastAttr.matches("[a-zA-Z]*[)]$")) {
                    attr.set(len - 1, lastAttr.substring(0, lastAttr.length() - 1));
                }
                if (attr.contains("unique")) {
                    uniqueKeys.add(attr.get(0));
                }
                attributes.add(createAttribute(attr));
            } else {
                throw new Exception("Invalid command!");
            }
        }
        Table table = new Table(tableName, tableName + ".bin", 0, attributes, primaryKeys, foreignKeys, uniqueKeys);
        addTableToCurrentDatabase(table);

        for (String uniqueKey : uniqueKeys) {
            createIndex(uniqueKey + ".ind", tableName, List.of(uniqueKey), true);
        }

        if (!primaryKeys.isEmpty()) {
            createIndex("primaryKeys.ind", tableName, primaryKeys, true);
        }
    }

    private boolean existAttributes(List<Attribute> attributes, List<String> primaryKeys) {
        List<String> attributeNames = attributes.stream()
                .map(Attribute::getAttributeName)
                .toList();
        return new HashSet<>(attributeNames).containsAll(primaryKeys);
    }

    private boolean existTable(String tableName) {
        return currentDatabase.getTableList().stream()
                .anyMatch(table -> table.getTableName().equals(tableName));
    }

    private boolean existRefKeys(String tableName, List<String> refKeys) {
        Optional<Table> refTable  = currentDatabase.getTableList().stream().
                filter(table -> table.getTableName().equals(tableName))
                .findFirst();
        if (refTable.isPresent()) {
            List<String> refTableAttributes = refTable.get().getAttributeList().stream().
                    map(Attribute::getAttributeName).toList();
            return new HashSet<>(refTableAttributes).containsAll(refKeys);
        } else {
            return false;
        }
    }
    private void addTableToCurrentDatabase(Table table) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("Database is not selected!");
        }
        if (currentDatabase.getTableList().stream().anyMatch(tab -> tab.getTableName().equals(table.getTableName()))) {
            throw new Exception("Table already exists!");
        }
        currentDatabase.addTable(table);
        updateCatalog();
        DatabaseSavingManager.saveCatalogToFile(catalog, PATHNAME);
    }
    private static Attribute createAttribute(List<String> attr) {
        String attributeName = attr.get(0);
        String type;
        int length = 0;
        boolean isNull = false;
        if (attr.get(1).contains("(")) {
            String[] typeSplit = attr.get(1).split("[()]");
            type = typeSplit[0];
            length = Integer.parseInt(typeSplit[1]);
        } else {
            type = attr.get(1);
        }
        if (attr.size() > 2) {
            if (attr.get(2).equals("not") && attr.get(3).equals("null")) {
                isNull = true;
            }
        }
        return new Attribute(attributeName, type, length, isNull);
    }

    private void useDatabase(String databaseName) throws Exception {
        List<Database> databaseList = catalog.getDatabaseList()
                .stream().filter(database -> database.getDatabaseName().equals(databaseName))
                .toList();
        if (databaseList.size() == 1) {
            currentDatabase = databaseList.get(0);
        } else {
            throw new Exception("Database doesn't exists!");
        }
    }

    private void createIndex(String indexName, String tableName, List<String> attributes, boolean isUnique) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("Database is not selected!");
        }
        IndexFile indexFile = new IndexFile(indexName, 20,  isUnique, "BTree", attributes);
        addIndexToCurrentDatabase(tableName, indexFile, attributes);

    }

    private void addIndexToCurrentDatabase(String tableName, IndexFile indexFile, List<String> attributes) throws Exception {
        boolean tableExists = false;
        for (Table table: currentDatabase.getTableList()) {
            if (table.getTableName().equals(tableName)) {
                if (table.getIndexFileList().stream().anyMatch(index -> index.getIndexName().equals(indexFile.getIndexName()))) {
                    throw new Exception("Index already exists!");
                } else if (!tableHasAttributes(table, attributes)) {
                    throw new Exception("Invalid attributes!");
                } else {
                    table.addIndex(indexFile);
                    addNewTableInCurrentDatabase(table);
                    updateCatalog();
                    DatabaseSavingManager.saveCatalogToFile(catalog, PATHNAME);
                    tableExists = true;
                    break;
                }
            }
        }

        if (!tableExists) {
            throw new Exception("Table doesn't exists!");
        }
    }

    private boolean tableHasAttributes(Table table, List<String> indexAttributes) {
        List<String> attributes = table.getAttributeList().stream()
                .map(Attribute::getAttributeName)
                .toList();
        return new HashSet<>(attributes).containsAll(indexAttributes);
    }

    private void createDatabase(String databaseName) throws Exception {
        if (catalog.getDatabaseList().stream().anyMatch(database -> database.getDatabaseName().equals(databaseName))) {
            throw new Exception("Database already exists!");
        }
        Database database = new Database(databaseName);
        catalog.addDatabase(database);
        DatabaseSavingManager.saveCatalogToFile(catalog, PATHNAME);
    }

    private void dropDatabase(String databaseName) throws Exception {
        if (catalog.getDatabaseList().stream().anyMatch(database -> database.getDatabaseName().equals(databaseName))) {
            List<Database> databases = catalog.getDatabaseList().stream()
                    .filter(database -> !database.getDatabaseName().equalsIgnoreCase(databaseName))
                    .toList();
            catalog.setDatabaseList(databases);
            DatabaseSavingManager.saveCatalogToFile(catalog, PATHNAME);
            if (currentDatabase.getDatabaseName().equals(databaseName)) {
                currentDatabase = null;
            }
        } else {
            throw new Exception("Database doesn't exists!");
        }
    }

    private void dropTable(String tableName) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("Database is not selected!");
        }
        if (currentDatabase.getTableList().stream().anyMatch(table -> table.getTableName().equals(tableName))) {
            List<Table> tables = currentDatabase.getTableList().stream()
                    .filter(table -> !table.getTableName().equalsIgnoreCase(tableName))
                    .collect(Collectors.toList());
            currentDatabase.setTableList(tables);
            updateCatalog();
            DatabaseSavingManager.saveCatalogToFile(catalog, PATHNAME);
        } else {
            throw new Exception("Table doesn't exists!");
        }
    }

    private void updateCatalog() {
        for (Database database : catalog.getDatabaseList()) {
            if (database.getDatabaseName().equals(currentDatabase.getDatabaseName())) {
                database.setTableList(currentDatabase.getTableList());
                break;
            }
        }
    }

    private void dropIndex(String indexName) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("Database is not selected!");
        }

        boolean indexExists = false;
        for (Table table : currentDatabase.getTableList()) {
            if (table.getIndexFileList().stream().anyMatch(index -> index.getIndexName().equals(indexName))) {
                List<IndexFile> indexFiles = table.getIndexFileList().stream()
                        .filter(indexFile -> !indexFile.getIndexName().equals(indexName))
                        .toList();
                table.setIndexFileList(indexFiles);
                addNewTableInCurrentDatabase(table);
                indexExists = true;
                updateCatalog();
                DatabaseSavingManager.saveCatalogToFile(catalog, PATHNAME);
                break;
            }
        }

        if (!indexExists){
            throw new Exception("Index doesn't exists!");
        }
    }

    private void addNewTableInCurrentDatabase(Table newTable) {
        List<Table> tables = new ArrayList<>(currentDatabase.getTableList().stream()
                .filter(table -> !table.getTableName().equals(newTable.getTableName()))
                .toList());
        tables.add(newTable);
        currentDatabase.setTableList(tables);
    }
}

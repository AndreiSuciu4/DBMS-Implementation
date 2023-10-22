package com.example.dbmsImplementation.entity;

import java.util.*;
public class Database {
    private String databaseName;
    private List<Table> tableList;

    public Database(String databaseName) {
        this.databaseName = databaseName;
        this.tableList = new ArrayList<>();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<Table> getTableList() {
        return tableList;
    }

    public void addTable(Table table) {
        tableList.add(table);
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }
}

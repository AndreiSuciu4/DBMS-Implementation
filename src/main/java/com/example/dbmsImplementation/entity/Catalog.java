package com.example.dbmsImplementation.entity;

import java.util.*;
public class Catalog {
    private List<Database> databaseList;

    public Catalog() {
        this.databaseList = new ArrayList<>();
    }

    public List<Database> getDatabaseList() {
        return databaseList;
    }

    public void setDatabaseList(List<Database> databaseList) {
        this.databaseList = databaseList;
    }

    public void addDatabase(Database database) {
        databaseList.add(database);
    }
}

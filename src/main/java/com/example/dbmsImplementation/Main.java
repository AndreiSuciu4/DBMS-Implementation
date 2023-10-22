package com.example.dbmsImplementation;

import com.example.dbmsImplementation.entity.*;
import com.example.dbmsImplementation.fileManager.DatabaseSavingManager;

import java.util.*;
public class Main {
    public static void main(String[] args) {
        Catalog catalog = new Catalog();
        Database universityDB = new Database("University");
        catalog.addDatabase(universityDB);

        Table table1 = new Table("Students", "students.xml", 0);
        Table table2 = new Table("Teachers", "teachers.xml", 0);
        universityDB.addTable(table1);
        universityDB.addTable(table2);

        Attribute attribute1 = new Attribute("firstname", "varchar", 100, false);
        Attribute attribute2 = new Attribute("lastname", "char", 50, true);
        Attribute attribute3 = new Attribute("money", "integer", 0, false);

        table1.setAttributeList(List.of(attribute1, attribute2, attribute3));
        table1.setPrimaryKey(List.of("firstname", "lastname"));
        table1.setUniqueKeyList(List.of("firstname", "lastname"));

        ForeignKey foreignKey1 = new ForeignKey(List.of("firstname"), "Teachers", List.of("firstname"));
        ForeignKey foreignKey2 = new ForeignKey(List.of("firstname", "lastname"), "Teachers", List.of("firstname, lastname"));

        table1.setForeignKeyList(List.of(foreignKey1, foreignKey2));

        IndexFile indexFile1 = new IndexFile("firstname.ind", 255, false, "BTree", List.of("firstname"));
        IndexFile indexFile2 = new IndexFile("name.ind", 50, true, "BTree", List.of("firstname", "lastname"));
        table1.setIndexFileList(List.of(indexFile1, indexFile2));
        DatabaseSavingManager.saveCatalogToFile(catalog);
    }
}

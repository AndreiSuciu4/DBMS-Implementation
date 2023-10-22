package com.example.dbmsImplementation.entity;

import java.util.*;
public class Table {
    private String tableName;
    private String filename;
    private int rowLength;
    private List<Attribute> attributeList;
    private List<String> primaryKey;
    private List<String> uniqueKeyList;
    private List<ForeignKey> foreignKeyList;
    private List<IndexFile> indexFileList;

    public Table(String tableName, String filename, int rowLength) {
        this.tableName = tableName;
        this.filename = filename;
        this.rowLength = rowLength;
        attributeList = new ArrayList<>();
        primaryKey = new ArrayList<>();
        uniqueKeyList = new ArrayList<>();
        foreignKeyList = new ArrayList<>();
        indexFileList = new ArrayList<>();
    }

    public Table(String tableName,
                 String filename,
                 int rowLength,
                 List<Attribute> attributeList,
                 List<String> primaryKey,
                 List<String> uniqueKeyList,
                 List<ForeignKey> foreignKeyList,
                 List<IndexFile> indexFileList) {
        this.tableName = tableName;
        this.filename = filename;
        this.rowLength = rowLength;
        this.attributeList = attributeList;
        this.primaryKey = primaryKey;
        this.uniqueKeyList = uniqueKeyList;
        this.foreignKeyList = foreignKeyList;
        this.indexFileList = indexFileList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getRowLength() {
        return rowLength;
    }

    public void setRowLength(int rowLength) {
        this.rowLength = rowLength;
    }

    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<String> getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(List<String> primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<String> getUniqueKeyList() {
        return uniqueKeyList;
    }

    public void setUniqueKeyList(List<String> uniqueKeyList) {
        this.uniqueKeyList = uniqueKeyList;
    }

    public List<ForeignKey> getForeignKeyList() {
        return foreignKeyList;
    }

    public void setForeignKeyList(List<ForeignKey> foreignKeyList) {
        this.foreignKeyList = foreignKeyList;
    }

    public List<IndexFile> getIndexFileList() {
        return indexFileList;
    }

    public void setIndexFileList(List<IndexFile> indexFileList) {
        this.indexFileList = indexFileList;
    }

    public void addAttribute(Attribute attribute) {
        attributeList.add(attribute);
    }
}

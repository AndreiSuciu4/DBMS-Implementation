package com.example.dbmsImplementation.entity;

import java.util.*;
public class IndexFile {
    private String indexName;
    private int keyLength;
    private boolean isUnique;
    private String indexType;
    private List<String> indexAttributeList;

    public IndexFile(String indexName, int keyLength, boolean isUnique, String indexType, List<String> indexAttributeList) {
        this.indexName = indexName;
        this.keyLength = keyLength;
        this.isUnique = isUnique;
        this.indexType = indexType;
        this.indexAttributeList = indexAttributeList;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public List<String> getIndexAttributeList() {
        return indexAttributeList;
    }

    public void setIndexAttributeList(List<String> indexAttributeList) {
        this.indexAttributeList = indexAttributeList;
    }
}

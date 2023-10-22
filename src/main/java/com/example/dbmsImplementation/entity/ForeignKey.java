package com.example.dbmsImplementation.entity;

import java.util.*;
public class ForeignKey {
    private List<String> attributeList;
    private String referencedTable;
    private List<String> referencedAttributeList;

    public ForeignKey(List<String> attributeList, String referencedTable, List<String> referencedAttributeList) {
        this.attributeList = attributeList;
        this.referencedTable = referencedTable;
        this.referencedAttributeList = referencedAttributeList;
    }

    public List<String> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<String> attributeList) {
        this.attributeList = attributeList;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public void setReferencedTable(String referencedTable) {
        this.referencedTable = referencedTable;
    }

    public List<String> getReferencedAttributeList() {
        return referencedAttributeList;
    }

    public void setReferencedAttributeList(List<String> referencedAttributeList) {
        this.referencedAttributeList = referencedAttributeList;
    }
}

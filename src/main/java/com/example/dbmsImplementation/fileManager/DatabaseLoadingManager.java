package com.example.dbmsImplementation.fileManager;

import com.example.dbmsImplementation.entity.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DatabaseLoadingManager {
    public static Catalog loadCatalogFromFile(String pathname) {
        Catalog catalog = new Catalog();
        try {
            Document document = parseXMLFile(pathname);
            catalog.setDatabaseList(parseDatabases(document));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return catalog;
    }

    private static Document parseXMLFile(String filePath)
            throws ParserConfigurationException, SAXException, IOException {
        File xmlFile = new File(filePath);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.parse(xmlFile);
    }

    private static List<Database> parseDatabases(Document document) {
        NodeList databaseNodes = document.getElementsByTagName("DataBase");
        List<Database> databaseList = new ArrayList<>();
        for (int i = 0; i < databaseNodes.getLength(); i++) {
            Element databaseElement = (Element) databaseNodes.item(i);
            databaseList.add(parseDatabaseElement(databaseElement));
        }
        return databaseList;
    }

    private static Database parseDatabaseElement(Element databaseElement) {
        String databaseName = databaseElement.getAttribute("dataBaseName");
        Database database = new Database(databaseName);
        NodeList tableNodes = databaseElement.getElementsByTagName("Table");
        database.setTableList(parseTables(tableNodes));
        return database;
    }

    private static List<Table> parseTables(NodeList tableNodes) {
        List<Table> tableList = new ArrayList<>();
        for (int i = 0; i < tableNodes.getLength(); i++) {
            Element tableElement = (Element) tableNodes.item(i);
            Table table = parseTable(tableElement);
            tableList.add(table);
        }
        return tableList;
    }

    private static Table parseTable(Element tableElement) {
        String tableName = tableElement.getAttribute("tableName");
        String fileName = tableElement.getAttribute("fileName");
        int rowLength = Integer.parseInt(tableElement.getAttribute("rowLength"));
        Table table = new Table(tableName, fileName, rowLength);

        Element structureElement = (Element) tableElement.getElementsByTagName("Structure").item(0);
        NodeList attributeNodes = structureElement.getElementsByTagName("Attribute");
        List<Attribute> attributes = parseAttributes(attributeNodes);
        table.setAttributeList(attributes);

        Element primaryKeyElement = (Element) tableElement.getElementsByTagName("PrimaryKey").item(0);
        List<String> primaryKey = parsePrimaryKey(primaryKeyElement);
        table.setPrimaryKey(primaryKey);

        Element foreignKeysElement = (Element) tableElement.getElementsByTagName("ForeignKeys").item(0);
        List<ForeignKey> foreignKeys = parseForeignKeys(foreignKeysElement);
        table.setForeignKeyList(foreignKeys);

        Element uniqueKeysElement = (Element) tableElement.getElementsByTagName("UniqueKeys").item(0);
        List<String> uniqueKeys = parseUniqueKeys(uniqueKeysElement);
        table.setUniqueKeyList(uniqueKeys);

        Element indexFilesElement = (Element) tableElement.getElementsByTagName("IndexFiles").item(0);
        List<IndexFile> indexFiles = parseIndexFiles(indexFilesElement);
        table.setIndexFileList(indexFiles);

        return table;
    }

    private static List<Attribute> parseAttributes(NodeList attributeNodes) {
        List<Attribute> attributeList = new ArrayList<>();
        for (int i = 0; i < attributeNodes.getLength(); i++) {
            Element attributeElement = (Element) attributeNodes.item(i);
            Attribute attribute = parseAttribute(attributeElement);
            attributeList.add(attribute);
        }
        return attributeList;
    }

    private static Attribute parseAttribute(Element attributeElement) {
        String attributeName = attributeElement.getAttribute("attributeName");
        String type = attributeElement.getAttribute("type");
        int length = Integer.parseInt(attributeElement.getAttribute("length"));
        boolean isNull = "1".equals(attributeElement.getAttribute("isnull"));
        return new Attribute(attributeName, type, length, isNull);
    }

    private static List<String> parsePrimaryKey(Element primaryKeyElement) {
        List<String> primaryKey = new ArrayList<>();
        NodeList primaryKeyAttributeNodes = primaryKeyElement.getElementsByTagName("PkAttribute");
        for (int i = 0; i < primaryKeyAttributeNodes.getLength(); i++) {
            Element pkAttributeElement = (Element) primaryKeyAttributeNodes.item(i);
            primaryKey.add(pkAttributeElement.getTextContent());
        }
        return primaryKey;
    }

    private static List<ForeignKey> parseForeignKeys(Element foreignKeysElement) {
        List<ForeignKey> foreignKeys = new ArrayList<>();
        NodeList foreignKeyNodes = foreignKeysElement.getElementsByTagName("ForeignKey");

        for (int i = 0; i < foreignKeyNodes.getLength(); i++) {
            Element foreignKeyElement = (Element) foreignKeyNodes.item(i);
            ForeignKey foreignKey = parseForeignKey(foreignKeyElement);
            foreignKeys.add(foreignKey);
        }

        return foreignKeys;
    }

    private static ForeignKey parseForeignKey(Element foreignKeyElement) {
        NodeList fkAttributeNodes = foreignKeyElement.getElementsByTagName("FkAttribute");
        List<String> fkAttributes = parseFkAttributes(fkAttributeNodes);

        Element referencesElement = (Element) foreignKeyElement.getElementsByTagName("References").item(0);
        Element refTableElement = (Element) referencesElement.getElementsByTagName("RefTable").item(0);
        String refTable = refTableElement.getTextContent();

        NodeList refAttributeNodes = referencesElement.getElementsByTagName("RefAttribute");
        List<String> refAttributes = parseRefAttributes(refAttributeNodes);

        return new ForeignKey(fkAttributes, refTable, refAttributes);
    }

    private static List<String> parseFkAttributes(NodeList fkAttributeNodes) {
        List<String> fkAttributes = new ArrayList<>();
        for (int i = 0; i < fkAttributeNodes.getLength(); i++) {
            Element fkAttributeElement = (Element) fkAttributeNodes.item(i);
            fkAttributes.add(fkAttributeElement.getTextContent());
        }
        return fkAttributes;
    }

    private static List<String> parseRefAttributes(NodeList refAttributeNodes) {
        List<String> refAttributes = new ArrayList<>();
        for (int i = 0; i < refAttributeNodes.getLength(); i++) {
            Element refAttributeElement = (Element) refAttributeNodes.item(i);
            refAttributes.add(refAttributeElement.getTextContent());
        }
        return refAttributes;
    }

    private static List<String> parseUniqueKeys(Element uniqueKeysElement) {
        List<String> uniqueKeys = new ArrayList<>();
        NodeList uniqueAttributeNodes = uniqueKeysElement.getElementsByTagName("UniqueAttribute");
        for (int i = 0; i < uniqueAttributeNodes.getLength(); i++) {
            Element uniqueAttributeElement = (Element) uniqueAttributeNodes.item(i);
            uniqueKeys.add(uniqueAttributeElement.getTextContent());
        }
        return uniqueKeys;
    }

    private static List<IndexFile> parseIndexFiles(Element indexFilesElement) {
        List<IndexFile> indexFiles = new ArrayList<>();
        NodeList indexFileNodes = indexFilesElement.getElementsByTagName("IndexFile");
        for (int i = 0; i < indexFileNodes.getLength(); i++) {
            Element indexFileElement = (Element) indexFileNodes.item(i);
            IndexFile indexFile = parseIndexFile(indexFileElement);
            indexFiles.add(indexFile);
        }
        return indexFiles;
    }
    private static IndexFile parseIndexFile(Element indexFileElement) {
        String indexName = indexFileElement.getAttribute("indexName");
        int keyLength = Integer.parseInt(indexFileElement.getAttribute("keyLength"));
        boolean isUnique = "1".equals(indexFileElement.getAttribute("isUnique"));
        String indexType = indexFileElement.getAttribute("indexType");

        IndexFile indexFile = new IndexFile(indexName, keyLength, isUnique, indexType);

        NodeList indexAttributeNodes = indexFileElement.getElementsByTagName("IAttribute");
        indexFile.setIndexAttributeList(parseIAttributes(indexAttributeNodes));
        return indexFile;
    }

    private static List<String> parseIAttributes(NodeList indexAttributeNodes) {
        List<String> iAttributes = new ArrayList<>();
        for (int j = 0; j < indexAttributeNodes.getLength(); j++) {
            Element indexAttributeElement = (Element) indexAttributeNodes.item(j);
            iAttributes.add(indexAttributeElement.getTextContent());
        }
        return iAttributes;
    }
}

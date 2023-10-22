package com.example.dbmsImplementation.fileManager;

import com.example.dbmsImplementation.entity.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.*;
public class DatabaseSavingManager {
    public static void saveCatalogToFile(Catalog catalog, String pathname) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document document = docBuilder.newDocument();
            Element databasesElement = saveDatabases(catalog.getDatabaseList(), document);
            document.appendChild(databasesElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new java.io.File(pathname));

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Element saveDatabases(List<Database> databaseList, Document document) {
        Element databasesElement = document.createElement("Databases");
        databaseList.forEach(database -> databasesElement.appendChild(saveDatabase(database, document)));
        return databasesElement;
    }

    private static Element saveDatabase(Database database, Document document) {
        Element databaseElement = document.createElement("DataBase");
        databaseElement.setAttribute("dataBaseName", database.getDatabaseName());
        databaseElement.appendChild(saveTables(database.getTableList(), document));
        return databaseElement;
    }

    private static Element saveTables(List<Table> tableList, Document document) {
        Element tablesElement = document.createElement("Tables");
        tableList.forEach(table -> tablesElement.appendChild(saveTable(table, document)));
        return tablesElement;
    }

    private static Element saveTable(Table table, Document document) {
        Element tableElement = document.createElement("Table");
        tableElement.setAttribute("tableName", table.getTableName());
        tableElement.setAttribute("fileName", table.getFilename());
        tableElement.setAttribute("rowLength", String.valueOf(table.getRowLength()));
        tableElement.appendChild(saveStructure(table.getAttributeList(), document));
        tableElement.appendChild(savePrimaryKey(table.getPrimaryKey(), document));
        tableElement.appendChild(saveForeignKeys(table.getForeignKeyList(), document));
        tableElement.appendChild(saveUniqueKeys(table.getUniqueKeyList(), document));
        tableElement.appendChild(saveIndexFiles(table.getIndexFileList(), document));
        return tableElement;
    }

    private static Element saveStructure(List<Attribute> attributeList, Document document) {
        Element stuctureElement = document.createElement("Structure");
        attributeList.forEach(attribute -> stuctureElement.appendChild(saveAttribute(attribute, document)));
        return stuctureElement;
    }

    private static Element saveAttribute(Attribute attribute, Document document) {
        Element attributeElement = document.createElement("Attribute");
        attributeElement.setAttribute("attributeName", attribute.getAttributeName());
        attributeElement.setAttribute("type", attribute.getType());
        attributeElement.setAttribute("length", String.valueOf(attribute.getLength()));
        attributeElement.setAttribute("isnull", attribute.isIsnull() ? "1" : "0");
        return attributeElement;
    }

    private static Element savePrimaryKey(List<String> primaryKey, Document document) {
        Element primaryKeyElement = document.createElement("PrimaryKey");
        primaryKey.forEach(pkAttribute -> primaryKeyElement.appendChild(savePkAttribute(pkAttribute, document)));
        return primaryKeyElement;
    }

    private static Element savePkAttribute(String primaryKey, Document document) {
        Element pkAttributeElement = document.createElement("PkAttribute");
        pkAttributeElement.appendChild(document.createTextNode(primaryKey));
        return pkAttributeElement;
    }

    private static Element saveForeignKeys(List<ForeignKey> foreignKeyList, Document document) {
        Element foreignKeysElement = document.createElement("ForeignKeys");
        foreignKeyList.forEach(foreignKey -> foreignKeysElement.appendChild(saveForeignKey(foreignKey, document)));
        return foreignKeysElement;
    }

    private static Element saveForeignKey(ForeignKey foreignKey, Document document) {
        Element foreignKeyElement = document.createElement("ForeignKey");
        foreignKey.getAttributeList()
                .forEach(fkAttribute -> foreignKeyElement.appendChild(saveFkAttribute(fkAttribute, document)));
        foreignKeyElement.appendChild(saveReferences(foreignKey.getReferencedTable(), foreignKey.getReferencedAttributeList(), document));
        return foreignKeyElement;
    }

    private static Element saveFkAttribute(String fkAttribute, Document document) {
        Element fkAttributeElement = document.createElement("FkAttribute");
        fkAttributeElement.appendChild(document.createTextNode(fkAttribute));
        return fkAttributeElement;
    }

    private static Element saveReferences(String referencedTable, List<String> referencedAttributeList, Document document) {
        Element referencesElement = document.createElement("References");
        Element refTableElement = document.createElement("RefTable");
        refTableElement.appendChild(document.createTextNode(referencedTable));
        referencesElement.appendChild(refTableElement);
        referencedAttributeList.forEach(refAttribute -> referencesElement.appendChild(saveRefAttribute(refAttribute, document)));
        return referencesElement;
    }

    private static Element saveRefAttribute(String refAttribute, Document document) {
        Element refAttributeElement = document.createElement("RefAttribute");
        refAttributeElement.appendChild(document.createTextNode(refAttribute));
        return refAttributeElement;
    }

    private static Element saveUniqueKeys(List<String> uniqueKeyList, Document document) {
        Element uniqueKeysElement = document.createElement("UniqueKeys");
        uniqueKeyList.forEach(uniqueAttribute -> uniqueKeysElement.appendChild(saveUniqueAttribute(uniqueAttribute, document)));
        return uniqueKeysElement;
    }

    private static Element saveUniqueAttribute(String uniqueAttribute, Document document) {
        Element uniqueAttributeElement = document.createElement("UniqueAttribute");
        uniqueAttributeElement.appendChild(document.createTextNode(uniqueAttribute));
        return uniqueAttributeElement;
    }

    private static Element saveIndexFiles(List<IndexFile> indexFileList, Document document) {
        Element indexFilesElement = document.createElement("IndexFiles");
        indexFileList.forEach(indexFile -> indexFilesElement.appendChild(saveIndexFile(indexFile, document)));
        return indexFilesElement;
    }

    private static Element saveIndexFile(IndexFile indexFile, Document document) {
        Element indexFileElement = document.createElement("IndexFile");
        indexFileElement.setAttribute("indexName", indexFile.getIndexName());
        indexFileElement.setAttribute("keyLength", String.valueOf(indexFile.getKeyLength()));
        indexFileElement.setAttribute("isUnique", indexFile.isUnique() ? "1": "0");
        indexFileElement.setAttribute("indexType", indexFile.getIndexType());
        indexFileElement.appendChild(saveIndexAttributes(indexFile.getIndexAttributeList(), document));
        return indexFileElement;
    }

    private static Element saveIndexAttributes(List<String> indexAttributes, Document document) {
        Element indexAttributesElement = document.createElement("IndexAttributes");
        indexAttributes
                .forEach(indexAttribute -> indexAttributesElement.appendChild(saveIndexAttribute(indexAttribute, document)));
        return indexAttributesElement;
    }

    private static Element saveIndexAttribute(String indexAttribute, Document document) {
        Element indexAttributeElement = document.createElement("IAttribute");
        indexAttributeElement.appendChild(document.createTextNode(indexAttribute));
        return indexAttributeElement;
    }
}

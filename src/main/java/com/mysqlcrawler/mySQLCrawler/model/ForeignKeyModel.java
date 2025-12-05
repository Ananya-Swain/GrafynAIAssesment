package com.mysqlcrawler.mySQLCrawler.model;

import lombok.Data;

@Data
public class ForeignKeyModel {
    private String columnName;
    private String referencedTable;
    private String referencedColumn;

    @Override
    public String toString() {
        return "\nColumn name : " + columnName + "\tReferenced column : " + referencedColumn + "\tReferenced table : " + referencedTable;
    }
}

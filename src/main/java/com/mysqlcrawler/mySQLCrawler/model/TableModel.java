package com.mysqlcrawler.mySQLCrawler.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableModel {
    private String tableName;
    private List<ColumnModel> columns = new ArrayList<>();
    private List<String> primaryKeys = new ArrayList<>();
    private List<ForeignKeyModel> foreignKeys = new ArrayList<>();
    private List<IndexModel> indexes = new ArrayList<>();

    @Override
    public String toString() {
        return "\nTable name : " + tableName + "\nColumns : " + columns + "\nPrimary Keys : " + primaryKeys + "\nForeign Keys : " + foreignKeys;
    }
}

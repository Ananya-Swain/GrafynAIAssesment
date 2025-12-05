package com.mysqlcrawler.mySQLCrawler.model;

import lombok.Data;

@Data
public class ColumnModel {
    private String name;
    private  String type;
    private int size;
    private boolean nullable;
    private boolean autoIncrement;

    @Override
    public String toString() {
        return "\nName : " + name + "\tType : " + type + "\tSize : " + size + "\tNullable : " + nullable + "\tAuto increment : " + autoIncrement;
    }
}

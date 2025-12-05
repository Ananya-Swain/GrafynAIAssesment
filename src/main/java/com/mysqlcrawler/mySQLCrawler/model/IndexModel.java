package com.mysqlcrawler.mySQLCrawler.model;


import lombok.Data;

@Data
public class IndexModel {
    private String name;
    private String columnName;
    boolean nonUnique;

    @Override
    public String toString() {
        return "\nName : " + name + "\tColumn name : " + columnName + "\tNon unique : " + nonUnique;
    }
}

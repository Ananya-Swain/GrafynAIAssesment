package com.mysqlcrawler.mySQLCrawler.model;

import lombok.Data;

@Data
public class UserConfig {
    private DatabaseConfig database;
    private CrawlerBehavior crawler;

    @Data
    public static class DatabaseConfig {
        private String jdbcUrl = "jdbc:mysql://localhost:3306/blogdb";
        private String username = "root";
        private String password = "Ananya19";
        private String driverClassName = "com.mysql.cj.jdbc.Driver";
    }

    @Data
    public static class CrawlerBehavior {
        private boolean includeViews = true;
        private boolean detectManyToMany = true;
        private boolean generateBidirectional = true;
        private String outputFolder = "src/main/java/com/mysqlcrawler/mySQLCrawler/generated";
    }
}

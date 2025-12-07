package com.mysqlcrawler.mySQLCrawler.model;

import lombok.Data;

@Data
public class UserConfig {
    private DatabaseConfig database;
    private CrawlerBehavior crawler;

    @Data
    public static class DatabaseConfig {
        private String jdbcUrl;
        private String username;
        private String password;
        private String driverClassName;
    }

    @Data
    public static class CrawlerBehavior {
        private boolean includeViews;
        private boolean detectManyToMany;
        private boolean generateBidirectional;
        private String outputFolder;
    }
}

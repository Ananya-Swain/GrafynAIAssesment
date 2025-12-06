package com.mysqlcrawler.mySQLCrawler.controller;

import com.mysqlcrawler.mySQLCrawler.model.UserConfig;
import com.mysqlcrawler.mySQLCrawler.service.CrawlerService;
import com.mysqlcrawler.mySQLCrawler.model.TableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metadata")
public class CrawlerController {

    @Autowired
    private final CrawlerService crawlerService;

    public CrawlerController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @PostMapping("tables")
    public List<String> getTables(@RequestBody UserConfig userConfig) throws Exception {
        return crawlerService.listTables(userConfig);
    }

    @PostMapping("/schema")
    public List<TableModel> getSchema(@RequestBody UserConfig userConfig) {
        return crawlerService.getTableSchema(userConfig);
    }
}

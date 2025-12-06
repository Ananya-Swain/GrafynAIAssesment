package com.mysqlcrawler.mySQLCrawler.controller;

import com.mysqlcrawler.mySQLCrawler.model.CrawlerConfig;
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
    public List<String> getTables(@RequestBody CrawlerConfig crawlerConfig) throws Exception {
        return crawlerService.listTables(crawlerConfig);
    }

    @PostMapping("/schema")
    public List<TableModel> getSchema(@RequestBody CrawlerConfig crawlerConfig) {
        return crawlerService.getTableSchema(crawlerConfig);
    }
}

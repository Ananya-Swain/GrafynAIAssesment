package com.mysqlcrawler.mySQLCrawler.controller;

import com.mysqlcrawler.mySQLCrawler.service.CrawlerService;
import com.mysqlcrawler.mySQLCrawler.model.TableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/metadata")
public class CrawlerController {

    @Autowired
    private final CrawlerService crawlerService;

    public CrawlerController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @GetMapping("tables")
    public List<String> getTables() throws Exception {
        return crawlerService.listTables();
    }

    @GetMapping("/schema")
    public List<TableModel> getSchema() {
        return crawlerService.getTableSchema();
    }
}

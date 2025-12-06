package com.mysqlcrawler.mySQLCrawler.controller;

import com.mysqlcrawler.mySQLCrawler.model.UserConfig;
import com.mysqlcrawler.mySQLCrawler.service.CrawlerService;
import com.mysqlcrawler.mySQLCrawler.model.TableModel;
import com.mysqlcrawler.mySQLCrawler.service.GetJsonFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metadata")
public class CrawlerController {

    @Autowired
    private final GetJsonFileService getJsonFileService;

    @Autowired
    private final CrawlerService crawlerService;

    public CrawlerController(CrawlerService crawlerService, GetJsonFileService getJsonFileService) {
        this.getJsonFileService = getJsonFileService;
        this.crawlerService = crawlerService;
    }

    @GetMapping("tables")
    public List<String> getTables() throws Exception {
        UserConfig userConfig = getJsonFileService.getConfig();
        return crawlerService.listTables(userConfig);
    }

    @GetMapping("/schema")
    public List<TableModel> getSchema() {
        UserConfig userConfig = getJsonFileService.getConfig();
        return crawlerService.getTableSchema(userConfig);
    }
}

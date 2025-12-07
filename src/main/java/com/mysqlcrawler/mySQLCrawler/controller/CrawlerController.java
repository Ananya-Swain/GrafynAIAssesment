package com.mysqlcrawler.mySQLCrawler.controller;

import com.mysqlcrawler.mySQLCrawler.model.UserConfig;
import com.mysqlcrawler.mySQLCrawler.service.CrawlerService;
import com.mysqlcrawler.mySQLCrawler.model.TableModel;
import com.mysqlcrawler.mySQLCrawler.service.GetJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metadata")
public class CrawlerController {

    @Autowired
    private final GetJsonService getJsonService;

    @Autowired
    private final CrawlerService crawlerService;

    public CrawlerController(CrawlerService crawlerService, GetJsonService getJsonService) {
        this.getJsonService = getJsonService;
        this.crawlerService = crawlerService;
    }

    @GetMapping("/tables")
    public List<String> getTables() throws Exception {
        UserConfig userConfig = getJsonService.getConfig();
        return crawlerService.listTables(userConfig);
    }

    @GetMapping("/schema")
    public List<TableModel> getSchema() {
        UserConfig userConfig = getJsonService.getConfig();
        return crawlerService.getTableSchema(userConfig);
    }
}

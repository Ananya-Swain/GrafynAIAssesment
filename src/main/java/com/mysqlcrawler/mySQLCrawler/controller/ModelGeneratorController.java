package com.mysqlcrawler.mySQLCrawler.controller;

import com.mysqlcrawler.mySQLCrawler.model.UserConfig;
import com.mysqlcrawler.mySQLCrawler.model.GeneratedTableInfo;
import com.mysqlcrawler.mySQLCrawler.model.TableModel;
import com.mysqlcrawler.mySQLCrawler.service.CrawlerService;
import com.mysqlcrawler.mySQLCrawler.service.GetJsonService;
import com.mysqlcrawler.mySQLCrawler.service.ModelGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/generate")
public class ModelGeneratorController {

    @Autowired
    private GetJsonService getJsonService;

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private ModelGeneratorService modelGeneratorService;

    @GetMapping("/models")
    public String generatedModels() {
        try {
            UserConfig userConfig = getJsonService.getConfig();
            List<TableModel> schema = crawlerService.getTableSchema(userConfig);
            List<GeneratedTableInfo> generatedTableInfoList = modelGeneratorService.generatedModels(schema, userConfig);

            return "Models generated successfully.\n" + generatedTableInfoList;
        }
        catch(IOException e) {
            e.printStackTrace();
            return "Failed to generate models: " + e.getMessage();
        }
    }
}

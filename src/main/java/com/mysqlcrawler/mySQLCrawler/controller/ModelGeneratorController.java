package com.mysqlcrawler.mySQLCrawler.controller;

import com.mysqlcrawler.mySQLCrawler.model.TableModel;
import com.mysqlcrawler.mySQLCrawler.service.CrawlerService;
import com.mysqlcrawler.mySQLCrawler.service.ModelGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/generate")
public class ModelGeneratorController {

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private ModelGeneratorService modelGeneratorService;

    @PostMapping("/models")
    public String generatedModels() {
        try {
            List<TableModel> schema = crawlerService.getTableSchema();
            modelGeneratorService.generatedModels(schema);

            return "Models generated successfully in: com.mysqlcrawler.mySQLCrawler.generated";
        }
        catch(IOException e) {
            e.printStackTrace();
            return "Failed to generate models: " + e.getMessage();
        }
    }
}

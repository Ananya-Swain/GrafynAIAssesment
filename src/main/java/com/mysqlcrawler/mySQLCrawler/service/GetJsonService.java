package com.mysqlcrawler.mySQLCrawler.service;

import com.mysqlcrawler.mySQLCrawler.model.UserConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

@Service
public class GetJsonService {
    private final ObjectMapper mapper = new ObjectMapper();

    public UserConfig getConfig() {
        try {
            ClassPathResource resource = new ClassPathResource("user-config.json");

            UserConfig userConfig = mapper.readValue(resource.getInputStream(), UserConfig.class);

            return userConfig;
        }
        catch(Exception e) {
            throw new RuntimeException("Failed to load user-config.json");
        }
    }
}

package com.zabzabdoda.services;

import com.zabzabdoda.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PosterService {

    @Autowired
    ConfigProperties configProperties;

    public String getPoster(String imdbUrl){
        try {
            String url = "https://movie-database-alternative.p.rapidapi.com/?r=json&i="+imdbUrl;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key",configProperties.apiKey());
            headers.set("X-RapidAPI-Host",configProperties.apiUrl());
            HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET,httpEntity,String.class);
            JsonParser springParser = JsonParserFactory.getJsonParser();
            Map<String, Object> g = springParser.parseMap(result.getBody());
            return (String) g.get("Poster");
        }catch (Exception e){
            return null;
        }

    }

}

package com.example.demo.config;

import com.example.demo.models.city.City;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Data
public class CityConfiguration {


    private final ResourceLoader resourceLoader;

    public CityConfiguration(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public List<String> citiesListString(List<City> cityList){

        System.out.println("Инициализация списка городов строкой");
        List<String> citiesListString = new ArrayList<>();

        for (City city : cityList) {
           citiesListString.add(city.getName().toLowerCase());
        }

        return citiesListString;
    }

    @Bean
    public Map<String, City> citiesMap(List<City> cityList){

        System.out.println("Инициализация хэш мапы городов");
        Map<String, City> citiesMap = new HashMap<>();

        for (City city : cityList) {
            citiesMap.put(city.getName().toLowerCase(), city);
        }

        return citiesMap;
    }





    @Bean
    public List<City> cityList() {
        Gson gson = new Gson();
        List<City> cities = null;
        try {
            System.out.println("Инициализация списка городов");

            // Загрузка ресурса (JSON-файла) из директории resources
            Resource resource = resourceLoader.getResource("classpath:russian-cities.json");

            // Чтение ресурса как потока данных
            try (Reader reader = new InputStreamReader(resource.getInputStream())) {
                // Определение типа для списка городов
                Type cityListType = new TypeToken<List<City>>() {}.getType();

                // Парсинг JSON в список объектов City
                cities = gson.fromJson(reader, cityListType);
            }

            // Вывод информации о каждом городе (если нужно)
//            for (City city : cities) {
//                System.out.println("City: " + city.getName());
//                System.out.println("District: " + city.getDistrict());
//                System.out.println("Subject: " + city.getSubject());
//                System.out.println("Population: " + city.getPopulation());
//                System.out.println("Coordinates: (" + city.getCoords().getLat() + ", " + city.getCoords().getLon() + ")");
//                System.out.println();
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }



}




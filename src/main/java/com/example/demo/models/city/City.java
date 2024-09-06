package com.example.demo.models.city;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class City {
    private String name;
    private String district;
    private String subject;
    private int population;
    private Coords coords;

}

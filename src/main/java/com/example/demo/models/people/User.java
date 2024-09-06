package com.example.demo.models.people;

import com.example.demo.models.city.City;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor

public class User {

    public User(Long userChatId) {
        this.userChatId = userChatId;
        blockedUsers = new ArrayList<>();
        this.statusUser =  StatusUser.NO_ACTIVE;
        this.steep = 0;
        this.dialogElizaCounter = 0;
    }

    City city;
    Integer age;
    Gender userGender;

    Integer fellowAgeMax;
    Integer fellowAgeMin;

    Gender fellowGender;

    Double lat;
    Double lon;

    StatusUser statusUser;
    Long userChatId;
    Long fellowChatId;


    Integer steep;
    Integer dialogElizaCounter;



    List<Long> blockedUsers;

    public void cleanRegisterInfo() {
        this.city = null;
        this.age = null;
        this.userGender = null;

        this.fellowAgeMax = null;
        this.fellowAgeMin = null;

        this.fellowGender = null;

        this.lat = null;
        this.lon = null;


    }

    public void appLevel() {
        this.steep ++;
    }

    public boolean checkNormal() {
        if (this.age < 75 && fellowAgeMin < 50) return true;

        else return false;


    }
}


package com.example.demo.models.people;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
public class UserInfoStorage {

    private Map<Long, User> userMap;

    private List<User> waitingList;


    private List<User> talkWithAlizaList;

    public UserInfoStorage() {
        System.out.println("Инициализация листа пользователей");
        this.userMap = new HashMap<>();
        this.waitingList = new ArrayList<>();
        this.talkWithAlizaList = new ArrayList<>();
    }



}

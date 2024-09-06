package com.example.demo.service;

import com.example.demo.config.BotConfig;
import com.example.demo.config.CityConfiguration;
import com.example.demo.models.city.City;
import com.example.demo.models.people.Gender;
import com.example.demo.models.people.StatusUser;
import com.example.demo.models.people.User;
import com.example.demo.models.people.UserInfoStorage;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchFellowService {


    @Autowired
    private List<String> citiesListString;

    @Autowired
    private Map<String, City> citiesMapName;
    @Autowired
    private BotConfig botConfig;

    @Autowired
    private CityConfiguration cityComponent;

    @Autowired
    private UserInfoStorage userInfoStorage;

    @Autowired
    private RegisterService registerService;


    @Autowired
    private Utils utils;
    

    public List<SendMessage> searchFellow(User user) {

        List<SendMessage> sendMessageList = new ArrayList<>();

        long chatId = user.getUserChatId();

        SendMessage response = new SendMessage();

        response.setChatId(String.valueOf(chatId));



        user.setStatusUser(StatusUser.WAITING_CHAT);
        response.setText("Отлично! Идет поиск собеседника... ");


        if (pairSearch(user)) {

            System.out.println("Чат юзера");
            System.out.println(user.getUserChatId());

            System.out.println("Чат собеседника ");
            System.out.println(user.getFellowChatId());

            response.setText("Собеседник найден! Можете начать разговор! ");
            response.setReplyMarkup(utils.getKeyboardButtons("Завершить общение"));
            sendMessageList.add(response);


            SendMessage responseForWaitingUser = new SendMessage();

            responseForWaitingUser.setText("Собеседник найден! Можете начать разговор! ");

            responseForWaitingUser.setChatId(String.valueOf(user.getFellowChatId()));

            responseForWaitingUser.setReplyMarkup(utils.getKeyboardButtons("Завершить общение"));

            sendMessageList.add(responseForWaitingUser);

        } else {
            userInfoStorage.getWaitingList().add(user);
            response.setText("Ожидайте собеседника... ");
            response.setReplyMarkup(utils.getKeyboardButtons("Отменить поиск собеседника"));
            sendMessageList.add(response);
        }
        return sendMessageList;
    }




    private boolean pairSearch(User user) {

        double distance = 15 + user.getSteep() * 30;


        User maxBestBallsUser = null;

        for (User userIntercourse : userInfoStorage.getWaitingList()) {

            if (userIntercourse.getUserChatId() == user.getUserChatId()) continue;

            if(user.getBlockedUsers().contains(userIntercourse.getUserChatId())) continue;

            if (userIntercourse.getAge() < user.getFellowAgeMin() || userIntercourse.getAge() > user.getFellowAgeMax())
                continue;

            if (user.getAge() < userIntercourse.getFellowAgeMin() || user.getAge() > userIntercourse.getFellowAgeMax())
                continue;


            if (!user.getFellowGender().equals(Gender.ANYWAY)) {
                if (user.getFellowGender() != userIntercourse.getUserGender()) continue;
            }

            if (!userIntercourse.getFellowGender().equals(Gender.ANYWAY)) {
                if (userIntercourse.getFellowGender() != user.getUserGender()) continue;
            }


            if (user.getAge() < userIntercourse.getFellowAgeMin() || user.getAge() > userIntercourse.getFellowAgeMax())
                continue;


            if (userIntercourse.getCity() != null && user.getCity() != null) {
                if (userIntercourse.getCity().getName() == user.getCity().getName() && maxBestBallsUser == null) {
                    distance = 1.5;
                    maxBestBallsUser = userIntercourse;
                }
            } else {
                double distanceCalculate = distanceCalculate(user.getLat(), user.getLon(), userIntercourse.getLat(), userIntercourse.getLon());
                if (distanceCalculate < distance) {
                    distance = distanceCalculate;
                    maxBestBallsUser = userIntercourse;
                }

            }

        }


        if (maxBestBallsUser != null) {
            user.setFellowChatId(maxBestBallsUser.getUserChatId());
            maxBestBallsUser.setFellowChatId(user.getUserChatId());

            userInfoStorage.getWaitingList().remove(user);
            userInfoStorage.getWaitingList().remove(maxBestBallsUser);

            maxBestBallsUser.setStatusUser(StatusUser.IN_CHAT);
            user.setStatusUser(StatusUser.IN_CHAT);

            return true;
        }

        return false;
    }


    public double distanceCalculate(double lat1, double lon1, double lat2, double lon2) {

        // Радиус Земли в километрах
        double earthRadius = 6371;

        // Преобразование широты и долготы в радианы
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lon1Rad = Math.toRadians(lon1);
        double lon2Rad = Math.toRadians(lon2);

        // Разница между широтой и долготой точек
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Вычисление расстояния с использованием формулы гаверсинусового расстояния
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        // Вывод расстояния между двумя точками
        System.out.println("Расстояние между двумя городами: " + distance + " км");

        return distance;
    }



}





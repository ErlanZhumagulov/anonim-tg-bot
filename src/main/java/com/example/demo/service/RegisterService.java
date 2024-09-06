package com.example.demo.service;

import com.example.demo.config.BotConfig;
import com.example.demo.config.CityConfiguration;
import com.example.demo.models.city.City;
import com.example.demo.models.city.Coords;
import com.example.demo.models.people.Gender;
import com.example.demo.models.people.StatusUser;
import com.example.demo.models.people.User;
import com.example.demo.models.people.UserInfoStorage;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RegisterService {


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
    private Utils utils;


    public SendMessage register(User user, Message message) {

        String text = message.getText();
        long chatId = message.getChatId();

        SendMessage response = new SendMessage();

        response.setChatId(String.valueOf(chatId));
        response.setReplyMarkup(utils.getKeyboardButtons());


        if (user.getLat() == null) {
            if (message.hasLocation()) {
                Location location = message.getLocation();
                processLocation(location, message.getChatId());

                user.setLat(location.getLatitude());
                user.setLon(location.getLongitude());
                response.setText("Сколько вам лет?");

                return response;


            } else if (citiesListString.contains(text.toLowerCase())) {
                user.setCity(citiesMapName.get(text.toLowerCase()));
                Coords coords = user.getCity().getCoords();
                user.setLat(Double.parseDouble(coords.getLat()));
                user.setLon(Double.parseDouble(coords.getLon()));

                response.setText("Ваш город : " + text + " \nСколько вам лет?");
                return response;

            } else {
                response.setText("Мы не смогли найти город" + text + " \nПопробуйте указать другой близкий к вам город");
                return response;
            }

        }


        if (user.getAge() == null) {
            if (tryParseInt(text)) {
                if (Integer.parseInt(text) > 15) {
                    user.setAge(Integer.parseInt(text));
                    response.setText("Вам : " + text + " лет \nСколько минимально лет должно быть вашему собеседнику (от 16 лет)? ");
                } else response.setText("Вам должно быть как минимум 16 лет, чтобы воспользоваться ботом ");
            } else {
                response.setText("Введите корректный возраст ");
            }
            return response;
        }

        if (user.getFellowAgeMin() == null) {
            if (tryParseInt(text)) {
                if (Integer.parseInt(text) > 15) {
                    user.setFellowAgeMin(Integer.parseInt(text));
                    response.setText("Минимальное количество лет вашему собеседнику : " + text + " лет \nСколько максимально лет может быть вашему собеседнику? ");
                } else
                    response.setText("Ваш собеседнику должно быть как минимум 16 лет. \nСколько минимально лет должно быть вашему собеседнику (от 16 лет)? ");

            } else {
                response.setText("Введите корректный возраст ");
            }
            return response;
        }


        if (user.getFellowAgeMax() == null) {
            if (tryParseInt(text)) {
                if (user.getFellowAgeMin() > Integer.parseInt(text)) {

                    user.setFellowAgeMin(null);

                    response.setText("Вы ввели некорректные значения возрастного диапазона поиска. \n" +
                            "Вам : " + user.getAge() + " лет \nСколько минимально лет должно быть вашему собеседнику? ");


                } else {

                    user.setFellowAgeMax(Integer.parseInt(text));
                    response.setText("Диапазон вашего поиска по возрасту: " + user.getFellowAgeMin() + "-" + " " + text + " лет \nКакого вы пола (парень/девушка)? ");
                    response.setReplyMarkup(utils.getKeyboardButtons("парень", "девушка"));
                }
            } else {
                response.setText("Введите корректный возраст ");
            }
            return response;
        }


        if (user.getUserGender() == null) {
            if (text.toLowerCase().contains("м") || text.toLowerCase().contains("парень")) {
                user.setUserGender(Gender.MALE);
                response.setText("Вы парень. С кем бы вы хотели общаться (парни/девушки/без разницы)?");
                response.setReplyMarkup(utils.getKeyboardButtons("парни", "девушки", "без разницы"));

                return response;

            } else if (text.toLowerCase().contains("ж") || text.toLowerCase().contains("дев")) {
                user.setUserGender(Gender.FEMALE);
                response.setText("Вы девушка. С кем бы вы хотели общаться (парни/девушки/без разницы)?");
                response.setReplyMarkup(utils.getKeyboardButtons("парни", "девушки", "без разницы"));


                return response;

            } else {
                response.setText("Введите корректный пол ");

                return response;

            }
        }


        if (user.getFellowGender() == null) {
            if (text.toLowerCase().contains("м") || text.toLowerCase().contains("пар")) {
                System.out.println("А почему?");
                user.setFellowGender(Gender.MALE);


            } else if (text.toLowerCase().contains("ж") || text.toLowerCase().contains("дев")) {
                System.out.println("ЭЙ, ДЕВУШКА, КРАСАВИЦА");
                user.setFellowGender(Gender.FEMALE);


            } else if (text.toLowerCase().contains("без разницы")) {
                user.setFellowGender(Gender.ANYWAY);


            } else {
                response.setText("Введите корректный пол ");

                return response;
            }

            StringBuilder informationUser = new StringBuilder();
            if (user.getCity() != null)
                informationUser.append("Вы из города " + user.getCity().getName() + "\n");
//                    else if (user.getLat() != null && user.getLon() != null) informationUser.append("Ваше местоположение " + hui_vam + "\n");
            else if (user.getLat() == null && user.getLon() == null)
                informationUser.append("Вы нвходитесь... А хер его знает, где вы там находитесь. Как ты сюда-то забрался?" + "\n");

            if (user.getAge() != null) informationUser.append("Вам " + user.getAge() + "\n");
            if (user.getUserGender().equals(Gender.MALE)) informationUser.append("Вы парень \n");
            else if (user.getUserGender().equals(Gender.FEMALE)) informationUser.append("Вы девушка \n");
            else informationUser.append("Вы чукча из анекдота + \n");

            if (user.getFellowGender().equals(Gender.MALE))
                informationUser.append("Ваш собеседник -- парень \n");
            else if (user.getFellowGender().equals(Gender.FEMALE))
                informationUser.append("Ваш собеседник -- девушка \n");
            else if (user.getFellowGender().equals(Gender.ANYWAY))
                informationUser.append("Вам все равно, с кем общаться \n");
            else informationUser.append("Вы ищите такого же чукчу \n");

            informationUser.append("Ваш диапазон поиска: " + user.getFellowAgeMin() + "-" + user.getFellowAgeMax() + " лет + \nВсе ли указано верно (Да/Нет)? ");
            response.setText(String.valueOf(informationUser));

            response.setReplyMarkup(utils.getKeyboardButtons("да", "нет"));

            return response;


        }


        if (true) {
            if (text.equals("да")) {
                response.setText("Отлично! Можете начинать общаться. Для этого отправьте команду 'Поиск собеседника' !  Приятного вам времяпрепровождения! ");
                response.setReplyMarkup(utils.getKeyboardButtons("Поиск собеседника"));
                user.setStatusUser(StatusUser.NO_ACTIVE);
                return response;

            } else if (text.equals("нет")) {
                user.cleanRegisterInfo();
                response.setText("Заполните информация о вас заново \nУкажите ваш город или можете укзать свое местоположение ");
                response.setReplyMarkup(createLocationKeyboard());


                return response;

            } else {
                response.setText("Введите корректное значение ответа (да/нет) ");
                response.setReplyMarkup(utils.getKeyboardButtons("да", "нет"));

                return response;
            }

        }


        return null;
    }


    private SendMessage processLocation(Location location, Long chatId) {
        System.out.println("Received location: " + location.getLatitude() + ", " + location.getLongitude());
        SendMessage confirmation = new SendMessage();
        confirmation.setChatId(String.valueOf(chatId));
        confirmation.setText("Спасибо за ваше местоположение: " + location.getLatitude() + ", " + location.getLongitude());
        return confirmation;
    }

    private ReplyKeyboardMarkup createLocationKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton button = new KeyboardButton();
        button.setText("Отправить местоположение");
        button.setRequestLocation(true);
        keyboardRow.add(button);
        keyboard.add(keyboardRow);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }




}








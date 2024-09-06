package com.example.demo.controllers;

import com.example.demo.config.BotConfig;
import com.example.demo.config.CityConfiguration;
import com.example.demo.models.city.City;
import com.example.demo.models.people.StatusUser;
import com.example.demo.models.people.User;
import com.example.demo.models.people.UserInfoStorage;
import com.example.demo.service.ChatService;
import com.example.demo.service.RegisterService;
import com.example.demo.service.SearchFellowService;
import com.example.demo.service.TelegramBot;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class WebhookController {

    @Autowired
    private Utils utils;

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
    private SearchFellowService searchFellowService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private ChatService chatService;
    private TelegramBot telegramBot;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @RequestMapping(value = "/callback/update", method = RequestMethod.POST)
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update) {


        if (update.hasMessage()) {

            Message message = update.getMessage();
            String text = message.getText();
            long chatId = message.getChatId();

            System.out.println("Сообщение. Текст: " + text);
            SendMessage response = new SendMessage();
            response.setChatId(String.valueOf(chatId));


            if (!userInfoStorage.getUserMap().containsKey(chatId)) {

                userInfoStorage.getUserMap().put(chatId, new User(chatId));
                System.out.println("Добавлен юзер с id " + chatId);
                response.setText("\n" +
                        "Приветствуем вас в нашем анонимном чате! \uD83C\uDF89\n" +
                        "\n" +
                        "\uD83D\uDD39 Здесь вы можете найти собеседников по городам и по полу, чтобы провести время интересно и увлекательно. \uD83C\uDF0D\uD83D\uDCAC\n" +
                        "\n" +
                        "\uD83D\uDD39 Чтобы начать, просто введите команду \"Начать регистрацию\" и следуйте указаниям. \uD83D\uDE80\n" +
                        "\n" +
                        "❗️ Обратите внимание:\n" +
                        "Ваши данные и переписки остаются полностью анонимными и защищёнными. Мы ценим вашу приватность! \uD83D\uDD12\n" +
                        "\n" +
                        "⚠️ Дисклеймер:\n" +
                        "\n" +
                        "Использование данного чата разрешено только лицам, достигшим 16 лет. \uD83D\uDD1E\n" +
                        "Запрещены любые формы оскорблений, угроз и ненадлежащего поведения. Будьте вежливы и уважительны друг к другу. \uD83D\uDEAB\n" +
                        "Администрация не несёт ответственности за содержание сообщений и поведение пользователей. \uD83E\uDD1D\n" +
                        "Готовы найти нового друга или просто пообщаться? Начинайте прямо сейчас! \uD83D\uDE0A\n\n " +
                        "P.S. Список городов будет пополняться. Если вашего города нет, просто укажите свое местоположение, либо укажите ближайший к вам город. Если никого из вашего города по указанным вами параметрам найти не удалось, то будет выбран ближайший к вам соответствующий собеседник. Это тестовая версия бота, в дальнейшем функционал будет дорабатываться! \uD83D\uDE0A ");

                response.setReplyMarkup(utils.getKeyboardButtons("Начать регистрацию"));

                utils.sendMessage(response);

                return ResponseEntity.ok().build();

            }

            // Ищем в мапе соответствующего юзера
            User user = userInfoStorage.getUserMap().get(chatId);
            System.out.println("Текущий статус   " + user.getStatusUser());

            StatusUser currentStatus = user.getStatusUser();


            switch (currentStatus) {


                case NO_ACTIVE -> {

                    if (!message.hasText()) {
                        response.setText("Введите корректный ответ ");
                        utils.sendMessage(response);
                        break;
                    }

                    switch (text) {

                        case "Начать регистрацию" -> {
                            user.setStatusUser(StatusUser.REGISTER);
                            user.cleanRegisterInfo();
                            response.setText("Начинается регистрация... ");
                            utils.sendMessage(response);

                            response.setText("Укажите ваш город или можете укзать свое местоположение ");
                            response.setReplyMarkup(utils.createLocationKeyboard());
                            utils.sendMessage(response);
                        }

                        case "Поиск собеседника" -> {
                            List<SendMessage> responsePair = searchFellowService.searchFellow(user);
                            for (SendMessage responseInList : responsePair) {
                                utils.sendMessage(responseInList);
                            }
                        }

                        default -> {
                            response.setText("Введите корректный ответ... ");
                            utils.sendMessage(response);
                        }
                    }

                }


                case IN_CHAT -> {
                    if ("Завершить общение".equals(text)) chatService.endTalk(user, response);
                    else chatService.sendMessageToFellow(message, user);
                }


                case REGISTER -> {

                    if (!message.hasText() && !message.hasLocation()) {
                        response.setText("Введите корректный ответ ");
                        utils.sendMessage(response);
                        break;

                    }


                    response = registerService.register(user, message);
                    utils.sendMessage(response);
                }


                case WAITING_CHAT -> {


                    if (!message.hasText()) {
                        response.setText("Введите корректный ответ ");
                        utils.sendMessage(response);
                        break;
                    }


                    if ("Отменить поиск собеседника".equals(text)) {
                        user.setStatusUser(StatusUser.NO_ACTIVE);
                        response.setReplyMarkup(utils.getKeyboardButtons("Поиск собеседника", "Начать регистрацию"));
                        response.setText("Поиск собеседника отменен \nМожете начать новый разговор или вернуться позже ");
                        utils.sendMessage(response);


                    }
                }


            }


        }

        return ResponseEntity.ok().build();
    }


}

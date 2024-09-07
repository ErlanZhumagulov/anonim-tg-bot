package com.example.demo.service;

import com.example.demo.config.BotConfig;
import com.example.demo.config.CityConfiguration;
import com.example.demo.controllers.WebhookController;
import com.example.demo.models.city.City;
import com.example.demo.models.people.UserInfoStorage;
import com.example.demo.utils.Utils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Data
@Component
public class TelegramBot extends TelegramWebhookBot {





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
    private WebhookController webhookController;
    @Autowired
    private Utils utils;

    @Autowired
    private SearchFellowService searchFellowService;

    @PostConstruct
    public void init(){

        webhookController.registerBot(this);
        utils.registerBot(this);

        var setWebhook = SetWebhook.builder()
                .url(botConfig.getBotUri())
                .build();







        try {
            this.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            System.out.println("Проблемы при инициализации бота");
        }


        List<BotCommand> commandList = new ArrayList<>();

        commandList.add(new BotCommand("/start", "Start the bot"));
        commandList.add(new BotCommand("/help", "Get help"));
        commandList.add(new BotCommand("/settings", "Settings"));

        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return "/update";
    }



}





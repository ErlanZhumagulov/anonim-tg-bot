package com.example.demo.service;

import com.example.demo.config.BotConfig;
import com.example.demo.config.CityConfiguration;
import com.example.demo.models.city.City;
import com.example.demo.models.people.StatusUser;
import com.example.demo.models.people.User;
import com.example.demo.models.people.UserInfoStorage;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {


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


    public void endTalk(User user, SendMessage response) {
        user.setStatusUser(StatusUser.NO_ACTIVE);
        response.setText("Беседа завершена \nМожете начать новый разговор или вернуться позже ");
        response.setReplyMarkup(utils.getKeyboardButtons("Поиск собеседника", "Начать регистрацию"));
        utils.sendMessage(response);

        if(user.getFellowChatId() != null){
            User userInterlocutor = userInfoStorage.getUserMap().get(user.getFellowChatId());
            userInterlocutor.setStatusUser(StatusUser.NO_ACTIVE);
            response.setChatId(String.valueOf(user.getFellowChatId()));
            response.setText("Собеседник завершил беседу \nМожете начать новый разговор или вернуться позже ");
            response.setReplyMarkup(utils.getKeyboardButtons("Поиск собеседника", "Начать регистрацию"));
            utils.sendMessage(response);
        } else{
            System.out.println("Неведомая херня");
        }


    }

    public void sendMessageToFellow(Message message, User user) {
        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(user.getFellowChatId()));

        if (message.hasText()) {
            response.setText(message.getText());

        } else if (message.hasVoice()) {
            SendVoice voice = new SendVoice();
            voice.setChatId(String.valueOf(user.getFellowChatId()));
            voice.setVoice(new InputFile(message.getVoice().getFileId()));
            utils.sendMessage(voice);
            return;

        } else if (message.hasDocument()) {

            SendDocument document = new SendDocument();
            document.setChatId(String.valueOf(user.getFellowChatId()));
            document.setCaption(message.getCaption());
            document.setDocument(new InputFile(message.getDocument().getFileId()));
            utils.sendMessage(document);
            return;

        } else if (message.hasSticker()) {
            SendSticker sticker = new SendSticker();
            sticker.setChatId(String.valueOf(user.getFellowChatId()));
            sticker.setSticker(new InputFile(message.getSticker().getFileId()));
            utils.sendMessage(sticker);
            return;

        } else if (message.hasPhoto()) {
            SendPhoto photo = new SendPhoto();
            photo.setChatId(String.valueOf(user.getFellowChatId()));
            photo.setCaption(message.getCaption());
            photo.setPhoto(new InputFile(message.getPhoto().get(0).getFileId()));
            utils.sendMessage(photo);
            return;

        } else if (message.hasAudio()) {
            SendAudio audio = new SendAudio();
            audio.setChatId(String.valueOf(user.getFellowChatId()));
            audio.setCaption(message.getCaption());
            audio.setAudio(new InputFile(message.getAudio().getFileId()));
            utils.sendMessage(audio);
            return;

        } else if (message.hasVideo()) {
            SendVideo video = new SendVideo();
            video.setChatId(String.valueOf(user.getFellowChatId()));
            video.setCaption(message.getCaption());
            video.setVideo(new InputFile(message.getVideo().getFileId()));
            utils.sendMessage(video);
            return;
        } else if (message.hasLocation()) {
            SendLocation location = new SendLocation();
            location.setChatId(String.valueOf(user.getFellowChatId()));
            location.setLatitude(message.getLocation().getLatitude());
            location.setLongitude(message.getLocation().getLongitude());
            utils.sendMessage(location);
            return;
        } else if (message.hasVideoNote()) {
            SendVideoNote videoNote = new SendVideoNote();
            videoNote.setChatId(String.valueOf(user.getFellowChatId()));
            videoNote.setVideoNote(new InputFile(message.getVideoNote().getFileId()));
            utils.sendMessage(videoNote);
            return;
        } else if (message.hasAnimation()) {
            SendAnimation animation = new SendAnimation();
            animation.setChatId(String.valueOf(user.getFellowChatId()));
            animation.setCaption(message.getCaption());
            animation.setAnimation(new InputFile(message.getAnimation().getFileId()));
            utils.sendMessage(animation);
            return;
        }


        utils.sendMessage(response);


    }




}





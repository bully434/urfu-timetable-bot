package com.clients;

import com.server.DatabaseOfSessions;
import com.server.GraphOfMessages;
import com.server.Message;
import com.server.notificator.NotificationManager;
import com.server.notificator.Notificator;
import com.server.User;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.List;

public class TelegramClient implements IClient {
    public static void initNewUser(String chatId, TelegramAPI api) {
        Message mes = GraphOfMessages.getInitMessage();
//        sendMessage.setText(mes.question);//только для консольного клиента, в tg будем получать token
        var operationId = mes.operationIdentifier;
        var transit = GraphOfMessages.getTransit(operationId);
        var user = new User(chatId, null, GraphOfMessages.getInitMessage(), null, new NotificationManager());
        DatabaseOfSessions.AddNewUserInDatabase(user);
        transit.accept(user);
        api.sendMessage(chatId, mes.question);
    }

    public void initSession(String chatId, TelegramAPI api) {
        Message mes = GraphOfMessages.getInitMessage();
        api.sendMessage(chatId, mes.question);
        var operationId = mes.operationIdentifier;
        var transit = GraphOfMessages.getTransit(operationId);
        var user = new User(chatId, null, GraphOfMessages.getInitMessage(), null, new NotificationManager());
        DatabaseOfSessions.AddNewUserInDatabase(user);
        transit.accept(user);
    }

    public void handleRequest(String chatId, String s, TelegramAPI api) {
        if (!DatabaseOfSessions.Contains(chatId)) {
            initSession(chatId, api);
        } else {
            var user = DatabaseOfSessions.GetUserByToken(chatId);
            user.lastAnswer = s;
            GraphOfMessages.getTransit(user.nextMessage.operationIdentifier).accept(user);
            var message = user.nextMessage;

            DatabaseOfSessions.UpdateUserInDatabase(user);
            api.sendMessage(chatId, message);
        }
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        var notificator = new Notificator();
        var thread = new Thread(notificator);
        thread.start();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TelegramAPI());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}

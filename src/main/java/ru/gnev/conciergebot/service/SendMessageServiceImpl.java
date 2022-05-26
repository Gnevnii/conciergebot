package ru.gnev.conciergebot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.gnev.conciergebot.ConciergeBot;

@Service
public class SendMessageServiceImpl implements SendMessageService{
    private final ConciergeBot conciergeBot;

    @Autowired
    public SendMessageServiceImpl(ConciergeBot conciergeBot) {
        this.conciergeBot = conciergeBot;
    }

    @Override
    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setText(message);

        try {
            conciergeBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

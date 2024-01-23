package ru.gnev.whereiscardsbot;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.DefaultSender;
import org.telegram.telegrambots.extensions.bots.timedbot.TimedSendLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.whereiscardsbot.config.CardBotConfiguration;

import java.io.PrintWriter;
import java.io.StringWriter;

@Component
public class WhereIsCardsBot extends TimedSendLongPollingBot {
    private static final Logger LOGGER = LogManager.getLogger(WhereIsCardsBot.class);

    private final CardBotConfiguration botConfiguration;

    public WhereIsCardsBot(CardBotConfiguration botConfiguration) {
        this.botConfiguration = botConfiguration;
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }

    @Override
    public void onUpdateReceived(final Update update) {
        try {
            //сохранить данные о чате, в который добавили бота
            if (update.hasMessage()) {

            } else if (update.hasCallbackQuery()) {

            }


            //сохранить/обновить данные о пользователе

            //если пользователя удаляют - пометить его удаленным

            //регистрация пользователя

            //команды бота работают с сообщениями или с фото. если такого нет - то и смысла нет перебирать все команды
            if (!isSupportedByBotMessage(update)) {
                return;
            }

            //команды

        } catch (Throwable throwable) {
            LOGGER.error(throwable);
        }
    }

    private String getStringException(final Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private boolean isSupportedByBotMessage(Update update) {
        if (update == null)
            return false;

        final Message message = update.getMessage();
        if (message == null)
            return false;

        return (StringUtils.isNotBlank(message.getText()) && message.getText().contains("#")) || message.hasPhoto();
    }

    @Override
    public void sendMessageCallback(Long chatId, Object messageRequest) {
        final DefaultSender defaultSender = new DefaultSender(this);
        try {
            if (messageRequest instanceof SendMessage) {
                defaultSender.execute((SendMessage) messageRequest);
            } else if (messageRequest instanceof ForwardMessage) {
                defaultSender.execute((ForwardMessage) messageRequest);
            } else if (messageRequest instanceof EditMessageReplyMarkup) {
                defaultSender.execute((EditMessageReplyMarkup) messageRequest);
            } else {
                throw new RuntimeException("No casting for type: " + messageRequest.getClass().getSimpleName());
            }
        } catch (Exception e) {
            // TODO: log error
        }
    }
}

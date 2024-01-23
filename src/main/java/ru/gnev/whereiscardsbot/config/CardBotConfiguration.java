package ru.gnev.whereiscardsbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CardBotConfiguration {
    private final String botToken;
    private final String botUsername;
    private final String creatorId;
    private final long creatorIdL;

    CardBotConfiguration(@Value("${bot.creatorId}") String creatorId,
                         @Value("${bot.username}") String botUsername,
                         @Value("${bot.token}") String botToken) {
        this.botToken = botToken.intern();
        this.botUsername = botUsername.intern();
        this.creatorId = creatorId.intern();
        this.creatorIdL = Long.parseLong(creatorId);
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public long getCreatorIdL() {
        return creatorIdL;
    }

    @Override
    public String toString() {
        return "BotConfiguration{" +
                "botToken='" + botToken + '\'' +
                ", botUsername='" + botUsername + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", creatorIdL=" + creatorIdL +
                '}';
    }
}

package ru.gnev.conciergebot.service;

import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.gnev.conciergebot.bean.QuestionKeyboardConfig;

import java.util.List;

public interface IRegistrationService {

    String processRegistration(long tgUserId, Message message);

    boolean isUserPartitionRegistered(long tgUserId, long tgChatId);

    String hasUnacceptableAnswers(long tgUserId);

    /**
     * Проверка зарегистрирован ли пользователь -
     * есть ли минимально необходимая информация о пользователе
     */
    boolean isUserRegistered(long tgUserId);

    @Transactional
    void registerBotInChat(Long tgUserId,
                           Long chatId);

    boolean isBotRegistered(Long chatId);
}

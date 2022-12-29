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

    /**
     * Ищет конфиг клавиатуры для следующего вопроса регистрации у бота, на который пользователь еще не отвечал
     */
    QuestionKeyboardConfig getNextRegistrationQuestionConfig(long tgUserId);

    /**
     * Ищет следующий по порядку вопрос регистрации у бота, на который пользователь еще не отвечал
     */
    RegistrationQuestion getNextRegistrationQuestion(User user);

    boolean isKnownUser(long tgUserId, long tgChatId);

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

    boolean isFromAddress(Long tgUserId);
}

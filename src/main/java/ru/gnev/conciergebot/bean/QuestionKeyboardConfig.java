package ru.gnev.conciergebot.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Конфигурация клавиатуры для отображения вопроса пользователю
 */
@Getter
@Setter
public class QuestionKeyboardConfig {
    private String question;
    private List<AnswerButtonConfig> buttonConfigs = new ArrayList<>();

    public QuestionKeyboardConfig(final String question) {
        this.question = question;
    }
}

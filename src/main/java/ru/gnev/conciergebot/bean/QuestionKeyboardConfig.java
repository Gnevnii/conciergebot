package ru.gnev.conciergebot.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Конфигурация клавиатуры для отображения вопроса пользователю
 */
@Getter
@Setter
public class QuestionKeyboardConfig {
    private String question;
    private List<AnswerButtonConfig> buttonConfigs;

    public QuestionKeyboardConfig(final String question, final List<AnswerButtonConfig> buttonConfigs) {
        this.question = question;
        this.buttonConfigs = buttonConfigs;
    }
}

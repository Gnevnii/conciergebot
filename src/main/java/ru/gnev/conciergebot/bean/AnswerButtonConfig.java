package ru.gnev.conciergebot.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * Данные для кнопки клавиатуры
 */
@Getter
@Setter
public class AnswerButtonConfig {
    /**
     * Текст на кнопке
     */
    private String label;

    /**
     * Данные, идентифицирующие кнопку
     */
    private String callbackData;

    public AnswerButtonConfig(final String label, final String callbackData) {
        this.label = label;
        this.callbackData = callbackData;
    }
}

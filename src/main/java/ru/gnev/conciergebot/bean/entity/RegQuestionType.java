package ru.gnev.conciergebot.bean.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * Тип вопроса регистрации у бота
 */
public enum RegQuestionType {
    NAME("name", "имя"),
    ADDRESS("address", "адрес"),
    FLOOR("floor", "этаж"),
    SECTION("section", "план"),
    FLAT("flat", "квартира");

    private String value;
    private String meaning;

    RegQuestionType(String value, String meaning) {
        this.value = value;
        this.meaning = meaning;
    }

    public String getValue() {
        return value;
    }

    public String getMeaning() {
        return meaning;
    }

    public static RegQuestionType findByMeaning(String meaning) {
        if (StringUtils.isBlank(meaning)) return null;
        for (RegQuestionType fc : RegQuestionType.values()) {
            if (fc.meaning.equals(meaning)) return fc;
        }
        return null;
    }
}

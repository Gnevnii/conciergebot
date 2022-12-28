package ru.gnev.conciergebot.bean.entity.registration;

/**
 * Тип значения ответа пользователя.
 */
public enum AnswerType {
    /**
     * Да/нет.
     */
    BOOLEAN("boolean"),
    /**
     * целочисленный ответ.
     */
    INTEGER("integer");

    /**
     * Значение
     */
    private final String value;

    AnswerType(final String vl) {
        this.value = vl;
    }

    public String getValue() {
        return value;
    }

    /**
     * Поиск объекта класса по значению
     */
    public static AnswerType findByValue(final String value) {
        for (AnswerType answerType : AnswerType.values()) {
            if (answerType.getValue().equals(value)) {
                return answerType;
            }
        }
        return null;
    }
}

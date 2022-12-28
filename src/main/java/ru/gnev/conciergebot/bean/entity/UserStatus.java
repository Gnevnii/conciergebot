package ru.gnev.conciergebot.bean.entity;

/**
 * Статус пользователя в тг чате.
 */
public enum UserStatus {
    LEFT("left"),
    MEMBER("member");

    private String value;

    UserStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

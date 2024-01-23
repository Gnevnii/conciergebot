package ru.gnev.whereiscardsbot.reaction;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum Command {
    REG("reg");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Command findByValue(String val) {
        for (Command value : Command.values()) {
            if (value.value.equals(val)) {
                return value;
            }
        }
        return null;
    }

    public static Command findByContainingValue(String value) {
        if (StringUtils.isBlank(value)) return null;

        List<Command> matching = new ArrayList<>();
        for (Command command : Command.values()) {
            if (value.contains(command.value)) {
                matching.add(command);
            }
        }

        if (matching.size() == 1) return matching.get(0);
        for (Command command : matching) {
            if (value.equals(command.value)) {
                return command;
            }
        }
        return null;
    }
}

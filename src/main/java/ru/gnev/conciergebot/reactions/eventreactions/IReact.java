package ru.gnev.conciergebot.reactions.eventreactions;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.FlatCoordinate;
import ru.gnev.conciergebot.bean.entity.Policy;

import java.util.List;
import java.util.function.BiConsumer;

public interface IReact {

    Policy getPolicy();

    boolean matchPolicy(Update update);

    boolean isValid(Update update);

    Command getCommand();

    void react(Update update, final BiConsumer<Long, Object> sender, final String commandLine);

    default String concatUserNames(final List<String> sameFloorUsers) {
        final List<String> strings = sameFloorUsers.stream().map(u -> "@" + u).toList();
        return String.join(", ", strings);
    }

    default boolean isValidNumber(final int number, final String pattern) {
        if (pattern.equals(FlatCoordinate.SECTION.getMeaning())) return number >= 1 && number <= 8;
        else return number >= 2 && number <= 25;
    }
}

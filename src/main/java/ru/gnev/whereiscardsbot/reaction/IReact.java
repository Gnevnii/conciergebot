package ru.gnev.whereiscardsbot.reaction;

import org.telegram.telegrambots.meta.api.objects.Update;
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
}

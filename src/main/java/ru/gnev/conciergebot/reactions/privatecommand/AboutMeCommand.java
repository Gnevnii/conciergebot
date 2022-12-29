package ru.gnev.conciergebot.reactions.privatecommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.bean.entity.User;
import ru.gnev.conciergebot.persist.repository.UserRepository;
import ru.gnev.conciergebot.reactions.eventreactions.AbstractPrivateChatReaction;

import java.util.List;
import java.util.function.BiConsumer;

@Component
public class AboutMeCommand extends AbstractPrivateChatReaction {

    private final UserRepository userRepository;

    @Autowired
    public AboutMeCommand(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Policy getPolicy() {
        return Policy.PRIVATE_CHAT;
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        final long userId = message.getFrom().getId();
        final User user = userRepository.getUserByTgUserId(userId);
        final List<String> userNames = userRepository.getUsersByFlatNumber(user.getFlatNumber())
                .stream()
                .filter(n -> !n.getTgUserName().equals(user.getTgUserName()))
                .map(u -> "@" + u.getTgUserName())
                .toList();

        String third = "";
        if (!userNames.isEmpty()) {
            third = "3. Помимо Вас в квартире зарегистрированы - " + String.join(", ", userNames);
        }

        String formatted = """
                Итак. Вот всё, что мне известно про Вас:
                1. Живете Вы в квартире №%s (это %sй этаж, план №%s);
                2. При регистрации Вы указали, что Вас зовут %s
                """.formatted(user.getFlatNumber(), user.getFloorNumber(), user.getSectionNumber(), user.getName());

        if (!third.isBlank())
            formatted += formatted + third;

        sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), formatted));
    }

    @Override
    public Command getCommand() {
        return Command.ABOUTME;
    }
}

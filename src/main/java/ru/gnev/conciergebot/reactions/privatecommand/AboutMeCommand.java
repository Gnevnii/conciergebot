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
        sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), """
                Ваш этаж - %s,
                Ваш план - %s
                """.formatted(user.getFloorNumber(), user.getSectionNumber())));
    }

    @Override
    public Command getCommand() {
        return Command.ABOUTME;
    }
}

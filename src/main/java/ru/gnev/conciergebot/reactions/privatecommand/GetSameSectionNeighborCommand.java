package ru.gnev.conciergebot.reactions.privatecommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.persist.repository.UserRepository;
import ru.gnev.conciergebot.reactions.eventreactions.AbstractPrivateChatReaction;

import java.util.function.BiConsumer;

@Component
public class GetSameSectionNeighborCommand extends AbstractPrivateChatReaction {
    private final UserRepository userRepository;

    @Autowired
    public GetSameSectionNeighborCommand(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Policy getPolicy() {
        return Policy.PRIVATE_CHAT;
    }

    @Override
    public boolean isValid(final Update update) {
        final boolean b = update.hasMessage();
        if (!b) return false;

        // TODO: должно быть снаружи. проверяться по Policy
        if (!update.getMessage().isUserMessage()) return false;
        return true;
    }

    @Override
    public Command getCommand() {
        return Command.NEIGHBOORSECTION;
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        final long userId = message.getFrom().getId();

        final String users = concatUserNames(userRepository.getSameSectionUsers(userId).stream()
                .filter(user -> !user.equals(message.getFrom().getUserName()))
                .toList());
        String text = users.isEmpty()
                ? "Извините, я пока ничего не знаю о Ваших соседях с таким же номером плана"
                : users + " - Ваши соседи с таким же номером плана";
        sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), text));
    }
}

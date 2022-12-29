package ru.gnev.conciergebot.reactions.privatecommand;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.FlatCoordinate;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.bean.entity.User;
import ru.gnev.conciergebot.persist.repository.UserRepository;
import ru.gnev.conciergebot.reactions.eventreactions.AbstractPrivateChatReaction;

import java.util.List;
import java.util.function.BiConsumer;

@Component
public class GetUsersByFlatCommand extends AbstractPrivateChatReaction {
    private final UserRepository userRepository;

    @Autowired
    public GetUsersByFlatCommand(final UserRepository userRepository) {
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
        return Command.FLAT;
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();

        final String flatN = commandLine.replaceAll(FlatCoordinate.FLAT.getMeaning(), "");
        final boolean isNumber = NumberUtils.isCreatable(flatN);
        if (isNumber) {
            final int flat = Integer.parseInt(flatN);
            if (flat >= 1 && flat <= 192) {
                final List<String> usersByNFlat = userRepository.getUsersByFlatNumber(flat)
                        .stream()
                        .filter(user -> user.getTgUserId() != message.getFrom().getId())
                        .map(User::getTgUserName)
                        .toList();
                if (usersByNFlat.isEmpty()) {
                    sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Из этой квартиры соседи у меня не зарегистрированы."));
                    return;
                }

                final String userNames = concatUserNames(usersByNFlat);
                sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), userNames + " - соседи из указанной квартиры"));
            } else {
                sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Неверный номер квартиры"));
            }
        } else {
            sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Неверный номер квартиры"));
        }
    }
}

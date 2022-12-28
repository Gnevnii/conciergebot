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
public class GetUsersByFloorNSectionCommand extends AbstractPrivateChatReaction {
    private final UserRepository userRepository;

    @Autowired
    public GetUsersByFloorNSectionCommand(final UserRepository userRepository) {
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

        // TODO: должно быть снаружи проверяться по Policy
        if (!update.getMessage().isUserMessage()) return false;
        return true;
    }

    @Override
    public Command getCommand() {
        return Command.SECTIONFLOORMIX;
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();

        final int firstPart;
        final int secondPart;
        final int floorIndx = commandLine.indexOf(FlatCoordinate.FLOOR.getMeaning());
        final int sectionIndx = commandLine.indexOf(FlatCoordinate.SECTION.getMeaning());

        String[] patterns = new String[2];
        patterns[0] = floorIndx < sectionIndx ? FlatCoordinate.SECTION.getMeaning() : FlatCoordinate.FLOOR.getMeaning();
        patterns[1] = floorIndx < sectionIndx ? FlatCoordinate.FLOOR.getMeaning() : FlatCoordinate.SECTION.getMeaning();

        String secondPartStr = commandLine.substring(commandLine.indexOf(patterns[0]));
        secondPartStr = secondPartStr.substring(patterns[0].length());
        boolean isNumber = NumberUtils.isCreatable(secondPartStr);
        if (!isNumber) {
            sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Неверный номер плана/этажа"));
            return;
        }

        secondPart = Integer.parseInt(secondPartStr);
        if (!isValidNumber(secondPart, patterns[0])) {
            sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Неверный номер плана/этажа"));
            return;
        }

        String firstPartStr = commandLine.replaceFirst(patterns[0] + secondPart, "").trim();
        firstPartStr = firstPartStr.substring(patterns[1].length());
        isNumber = NumberUtils.isCreatable(firstPartStr);
        if (!isNumber) {
            sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Неверный номер плана/этажа"));
            return;
        }

        firstPart = Integer.parseInt(firstPartStr);
        if (!isValidNumber(firstPart, patterns[1])) {
            sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Неверный номер плана/этажа"));
            return;
        }

        final List<User> users = userRepository.getUsersByFloorNumberAndSectionNumber(patterns[0].equals(FlatCoordinate.FLOOR.getMeaning()) ? secondPart : firstPart,
                patterns[0].equals(FlatCoordinate.FLOOR.getMeaning()) ? firstPart : secondPart);
        users.removeIf(u -> u.getTgUserId() == message.getFrom().getId());
        if (users.isEmpty()) {
            sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Соседи с таким номером плана/этажа у меня не зарегистрированы."));
            return;
        }

        final String userNames = concatUserNames(users.stream()
                .map(User::getTgUserName)
                .toList());
        sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), userNames + " - соседи с указанного плана/этажа"));
    }
}

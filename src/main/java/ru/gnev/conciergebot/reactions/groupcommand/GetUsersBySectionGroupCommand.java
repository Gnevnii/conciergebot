package ru.gnev.conciergebot.reactions.groupcommand;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.bean.entity.User;
import ru.gnev.conciergebot.persist.repository.UserRepository;
import ru.gnev.conciergebot.service.IRegistrationService;

import java.util.List;
import java.util.function.BiConsumer;

@Component
public class GetUsersBySectionGroupCommand extends AbstractGroupChatReaction {
    private final UserRepository userRepository;

    public GetUsersBySectionGroupCommand(final UserRepository userRepository, final IRegistrationService registrationService) {
        super(registrationService);
        this.userRepository = userRepository;
    }

    @Override
    public Policy getPolicy() {
        return Policy.SUPERGROUP_CHAT;
    }

    @Override
    public boolean isValid(final Update update) {
        return update.hasMessage();
    }

    @Override
    public Command getCommand() {
        return Command.SECTION;
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        if (isNotRegisteredUser(message.getFrom().getId())) {
            sendNotRegisteredMessage(sender, message);
            return;
        }

        final String sectionN = commandLine.replaceAll(getCommand().getValue(), "");
        final boolean isNumber = NumberUtils.isCreatable(sectionN);
        if (isNumber) {
            final int section = Integer.parseInt(sectionN);
            if (section >= 1 && section <= 8) {
                final List<String> usersByNFloor = userRepository.getUsersBySectionNumber(section)
                        .stream()
                        .filter(user -> user.getTgUserId() != message.getFrom().getId())
                        .map(User::getTgUserName)
                        .toList();
                if (usersByNFloor.isEmpty()) {
                    sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Соседи с таким номером плана у меня не зарегистрированы."));
                    return;
                }

                final String userNames = concatUserNames(usersByNFloor);
                sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), userNames + ", вас позвали."));
            } else {
                sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Неверный номер плана"));
            }
        } else {
            sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Неверный номер плана"));
        }
    }
}

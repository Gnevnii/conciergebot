package ru.gnev.conciergebot.reactions.eventreactions;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.bean.entity.User;
import ru.gnev.conciergebot.persist.repository.UserRepository;

import java.util.function.BiConsumer;


/**
 * NOTE: реакция должна быть строго для группового чата - дает понимание, что пользователь уже состоит в групповом чате.
 * Далее, в личном чате можно будет зарегистрировать тех пользователей, которые состоят в групповом чате.
 * Т.е. если пользователя нет в групповом чате, значит он не должен мочь зарегистрироваться у бота.
 */
@Component
public class UpdateUserInfoReaction implements IReact {
    private final UserRepository userRepository;

    @Autowired
    public UpdateUserInfoReaction(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(final Update update) {
        final Message message = update.getMessage();
        if (message == null) return false;

        final org.telegram.telegrambots.meta.api.objects.User tgUser = message.getFrom();
        if (tgUser == null) return false;

        User user = userRepository.getUserByTgUserId(tgUser.getId());
        if (user == null) return true;

        return !user.getTgUserName().equals(tgUser.getUserName());
    }

    @Override
    public boolean matchPolicy(Update update) {
        //не менять принадлежность к групповому чату, см. javadoc к классу
        return update.hasMessage() && (update.getMessage().isSuperGroupMessage() || update.getMessage().isGroupMessage());
    }

    @Override
    public Command getCommand() {
        return Command.REACTION;
    }

    @Override
    public Policy getPolicy() {
        return Policy.SUPERGROUP_CHAT;
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        final org.telegram.telegrambots.meta.api.objects.User tgUser = message.getFrom();
        User user = userRepository.getUserByTgUserId(tgUser.getId());
        if (user == null) {
            user = getUser(message.getChatId(), tgUser);
        }
        if (!user.getTgUserName().equals(tgUser.getUserName())) {
            user.setTgUserName(tgUser.getUserName());
        }
        userRepository.save(user);
    }

    @NotNull
    protected User getUser(final long chatId,
                           final org.telegram.telegrambots.meta.api.objects.User tgUser) {
        User user = new User();
        user.setTgGroupChatId(chatId);
        user.setTgUserName(tgUser.getUserName());
        user.setTgUserId(tgUser.getId());
        user.setDeleted(false);
        return user;
    }
}

package ru.gnev.conciergebot.reactions.eventreactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.bean.entity.User;
import ru.gnev.conciergebot.bean.entity.UserStatus;
import ru.gnev.conciergebot.config.BotConfiguration;
import ru.gnev.conciergebot.persist.repository.UserRepository;
import ru.gnev.conciergebot.service.IRegistrationService;

import java.util.function.BiConsumer;

@Component
public class BotAddedToChatReaction implements IReact {
    private final BotConfiguration botConfiguration;
    private final IRegistrationService registrationService;
    private final UserRepository userRepository;

    @Autowired
    public BotAddedToChatReaction(final BotConfiguration botConfiguration,
                                  final IRegistrationService registrationService,
                                  final UserRepository userRepository) {
        this.botConfiguration = botConfiguration;
        this.registrationService = registrationService;
        this.userRepository = userRepository;
    }

    @Override
    public Policy getPolicy() {
        return Policy.SUPERGROUP_CHAT;
    }

    @Override
    public boolean matchPolicy(Update update) {
        final ChatMemberUpdated myChatMember = update.getMyChatMember();
        return myChatMember != null && (myChatMember.getChat().isSuperGroupChat() || myChatMember.getChat().isGroupChat());
    }

    @Override
    public Command getCommand() {
        return Command.REACTION;
    }

    @Override
    public boolean isValid(final Update update) {
        final boolean b = update.hasMessage();
        if (b) return false;

        final ChatMemberUpdated myChatMember = update.getMyChatMember();

        final ChatMember newChatMember = myChatMember.getNewChatMember();
        final ChatMember oldChatMember = myChatMember.getOldChatMember();
        if (newChatMember == null || oldChatMember == null) return false;

        final boolean isCurrentBot = botConfiguration.getBotUsername().equals(newChatMember.getUser().getUserName());
        if (!isCurrentBot) return false;

        boolean wasLeft = UserStatus.LEFT.getValue().equals(oldChatMember.getStatus());
        if (!wasLeft) return false;

        boolean nowIsMember = UserStatus.MEMBER.getValue().equals(newChatMember.getStatus());
        if (!nowIsMember) return false;

        return !registrationService.isBotRegistered(myChatMember.getChat().getId());
    }

    @Override
    @Transactional
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final ChatMemberUpdated myChatMember = update.getMyChatMember();
        registrationService.registerBotInChat(myChatMember.getFrom().getId(), myChatMember.getChat().getId());

        //сохранить пользователя, добавившего бота
        final org.telegram.telegrambots.meta.api.objects.User tgUser = myChatMember.getFrom();
        User user = userRepository.getUserByTgUserId(tgUser.getId());
        if (user == null) {
            user = new User();
            user.setTgGroupChatId(myChatMember.getChat().getId());
            user.setTgUserName(tgUser.getUserName());
            user.setTgUserId(tgUser.getId());
            user.setDeleted(false);
        }
        userRepository.save(user);
    }
}

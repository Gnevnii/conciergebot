package ru.gnev.conciergebot.reactions.eventreactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.bean.entity.UserStatus;
import ru.gnev.conciergebot.persist.repository.UserRepository;
import ru.gnev.conciergebot.service.DeleteService;

import java.util.function.BiConsumer;

@Component
public class UserRemovedFromChatReaction implements IReact {
    private final DeleteService deleteService;
    private final UserRepository userRepository;

    @Autowired
    public UserRemovedFromChatReaction(final DeleteService deleteService, final UserRepository userRepository) {
        this.deleteService = deleteService;
        this.userRepository = userRepository;
    }

    @Override
    public Policy getPolicy() {
        return Policy.SUPERGROUP_CHAT;
    }

    @Override
    public boolean matchPolicy(final Update update) {
        final ChatMemberUpdated mychatMember = update.getMyChatMember();
        return mychatMember != null && (mychatMember.getChat().isSuperGroupChat() || mychatMember.getChat().isGroupChat());
    }

    @Override
    public boolean isValid(final Update update) {
        final boolean b = update.hasMessage();
        if (b) return false;

        final ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
        final ChatMember oldChatMember = update.getMyChatMember().getOldChatMember();
        if (newChatMember == null || oldChatMember == null) return false;

        boolean wasMember = UserStatus.MEMBER.getValue().equals(oldChatMember.getStatus());
        if (!wasMember) return false;

        boolean isNowLeft = UserStatus.LEFT.getValue().equals(newChatMember.getStatus());
        if (!isNowLeft) return false;

        return userRepository.existsByTgUserId(newChatMember.getUser().getId());
    }

    @Override
    @Transactional
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final ChatMember oldChatMember = update.getMyChatMember().getOldChatMember();
        deleteService.markDeleted(oldChatMember.getUser().getId());
    }

    @Override
    public Command getCommand() {
        return Command.REACTION;
    }
}

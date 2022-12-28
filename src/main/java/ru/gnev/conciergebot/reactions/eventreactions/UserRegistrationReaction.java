package ru.gnev.conciergebot.reactions.eventreactions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.service.IRegistrationService;

import java.util.function.BiConsumer;

/**
 * Реакция строго для личного чата с ботом.
 * 1. Пользователь сначала полурегистрируется в групповом чате - (это даст понимание, что он состоит в целевом групповом чате).
 * 2. Далее пользователь может регистрироваться в личном чате у бота.
 */
@Component
public class UserRegistrationReaction extends AbstractPrivateChatReaction {
    private final IRegistrationService registrationService;

    @Autowired
    public UserRegistrationReaction(final IRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public Policy getPolicy() {
        return Policy.PRIVATE_CHAT;
    }

    @Override
    public boolean isValid(final Update update) {
        if (update == null || !update.hasMessage()) return false;

        final Message message = update.getMessage();
        return !registrationService.isUserRegistered(message.getFrom().getId());
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        final boolean userPartitionRegistered = registrationService.isUserPartitionRegistered(message.getFrom().getId(), message.getChatId());
        if (!userPartitionRegistered) {
            sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Не видел Вас в чате дома. Вы там что-нибудь когда-нибудь писали? Напишите Ы"));
            return;
        }

        final String nextQuestionText = registrationService.processRegistration(message.getFrom().getId(), message);
        if (StringUtils.isBlank(nextQuestionText)) return;

        sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), nextQuestionText));
    }

    @Override
    public Command getCommand() {
        return Command.REACTION;
    }
}

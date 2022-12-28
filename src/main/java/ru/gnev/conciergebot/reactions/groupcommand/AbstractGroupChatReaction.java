package ru.gnev.conciergebot.reactions.groupcommand;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.reactions.eventreactions.AbstractHasMessageReaction;
import ru.gnev.conciergebot.reactions.eventreactions.IReact;
import ru.gnev.conciergebot.service.IRegistrationService;

import java.util.function.BiConsumer;

@Component
@AllArgsConstructor
public abstract class AbstractGroupChatReaction extends AbstractHasMessageReaction {

    protected final IRegistrationService registrationService;

    @Override
    public boolean matchPolicy(Update update) {
        final Message message = update.getMessage();
        return (message.isSuperGroupMessage() || message.isGroupMessage()) && (Policy.GROUP_CHAT == getPolicy() || Policy.SUPERGROUP_CHAT == getPolicy());
    }

    protected void sendNotRegisteredMessage(final BiConsumer<Long, Object> sender, final Message message) {
        final SendMessage sendMessage = new SendMessage(String.valueOf(message.getChatId()), "Вы не зарегистрированы у бота. Напишите ему в личку.");
        sendMessage.setReplyToMessageId(message.getMessageId());
        sender.accept(message.getChatId(), sendMessage);
    }

    protected boolean isNotRegisteredUser(long tgUserid) {
        return !registrationService.isUserRegistered(tgUserid);
    }
}

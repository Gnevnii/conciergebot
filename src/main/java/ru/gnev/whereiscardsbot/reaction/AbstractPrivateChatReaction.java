package ru.gnev.whereiscardsbot.reaction;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.entity.Policy;

@Component
public abstract class AbstractPrivateChatReaction extends AbstractHasMessageReaction {

    @Override
    public boolean matchPolicy(Update update) {
        final Message message = update.getMessage();
        return message.isUserMessage() && Policy.PRIVATE_CHAT == getPolicy();
    }
}

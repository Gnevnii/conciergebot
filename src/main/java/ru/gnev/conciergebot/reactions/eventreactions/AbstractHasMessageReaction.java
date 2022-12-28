package ru.gnev.conciergebot.reactions.eventreactions;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public abstract class AbstractHasMessageReaction implements IReact {

    @Override
    public boolean isValid(final Update update) {
        return update.hasMessage();
    }
}

package ru.gnev.conciergebot.reactions.eventreactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.config.BotConfiguration;

import java.util.function.BiConsumer;

@Component
public class BotRecievePhotoReaction implements IReact {
    private final BotConfiguration botConfiguration;

    @Autowired
    public BotRecievePhotoReaction(final BotConfiguration botConfiguration) {
        this.botConfiguration = botConfiguration;
    }

    @Override
    public Policy getPolicy() {
        return Policy.PRIVATE_CHAT;
    }

    @Override
    public boolean matchPolicy(Update update) {
        return update.getMessage().isUserMessage() && Policy.PRIVATE_CHAT == getPolicy();
    }

    @Override
    public Command getCommand() {
        return Command.PHOTO_SENT;
    }

    @Override
    public boolean isValid(final Update update) {
        if (!update.hasMessage()) return false;
        return update.getMessage().hasPhoto();
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        sender.accept(Long.valueOf(botConfiguration.getCreatorId()), new ForwardMessage(botConfiguration.getCreatorId(), String.valueOf(message.getChatId()), message.getMessageId()));
        sender.accept(Long.valueOf(botConfiguration.getCreatorId()), new SendMessage(botConfiguration.getCreatorId(), "Сообщение от пользователя @" + message.getFrom().getUserName()));
    }
}

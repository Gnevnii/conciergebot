package ru.gnev.conciergebot.reactions.privatecommand;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.reactions.eventreactions.AbstractPrivateChatReaction;

import java.util.function.BiConsumer;

@Component
public class AddressCommand extends AbstractPrivateChatReaction {
    @Override
    public Policy getPolicy() {
        return Policy.PRIVATE_CHAT;
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), """
                ул. Саларьевская, дом 8, корпус 2
                (ЖК "Саларьево Парк", корпус 2 или 23-й км Киевского шоссе 5к2).
                Индекс - https://gprivate.com/62fvu"""));
    }

    @Override
    public Command getCommand() {
        return Command.ADDRESS;
    }
}

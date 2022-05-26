package ru.gnev.conciergebot.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.service.SendMessageService;

@Component
public class UnknownCommand implements ICommand {

    private final SendMessageService sendMessageService;

    public final static String MESSAGE = """
            Неизвестная команда.
            Чтобы посмотреть список команд введите /help""";

    @Autowired
    public UnknownCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(final Update update) {
        sendMessageService.sendMessage(update.getMessage().getChatId().toString(), MESSAGE);
    }
}

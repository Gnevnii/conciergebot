package ru.gnev.conciergebot.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.service.SendMessageService;

@Component
public class HelpCommand implements ICommand {

    private final SendMessageService sendMessageService;

    public final static String MESSAGE = String.format("""
                    Доступные команды
                    + Начать закончить работу с ботом
                    + "%s - начать работу со мной
                    + "%s - приостановить работу со мной
                    + "%s - получить помощь в работе со мной""",
            CommandName.START.getCommandName(), CommandName.STOP.getCommandName(), CommandName.HELP.getCommandName());

    @Autowired
    public HelpCommand(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void execute(final Update update) {
        sendMessageService.sendMessage(update.getMessage().getChatId().toString(), MESSAGE);
    }
}

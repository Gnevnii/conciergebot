package ru.gnev.conciergebot.command;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gnev.conciergebot.service.SendMessageService;

@Component
public class CommandContainer {
    private final ImmutableMap<String, ICommand> commandMap;
    private final ICommand unknownCommand;

    @Autowired
    public CommandContainer(SendMessageService sendMessageService) {
        commandMap = ImmutableMap.<String, ICommand>builder()
                .put(CommandName.START.getCommandName(), new StartCommand(sendMessageService))
                .put(CommandName.STOP.getCommandName(), new StopCommand(sendMessageService))
                .put(CommandName.HELP.getCommandName(), new HelpCommand(sendMessageService))
                .put(CommandName.NO.getCommandName(), new NoCommand(sendMessageService))
                .build();

        unknownCommand = new UnknownCommand(sendMessageService);
    }

    public ICommand retrieveCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, unknownCommand);
    }
}

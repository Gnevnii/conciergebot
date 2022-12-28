package ru.gnev.conciergebot.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.gnev.conciergebot.bean.Command;

@Service
public class CommandResolver implements ICommandResolver {
    @Override
    public CommandAndCommandLine resolveCommandInfo(final String message) {
        if (message == null) return null;

        final int i = message.indexOf("#");
        if (i == -1) return null;

        String startCommandLine = message.substring(i);
        startCommandLine = startCommandLine.replaceAll(",", " ")
                .replaceAll("\\.", " ");
        final int length = startCommandLine.length();
        final int spaceIndex = startCommandLine.indexOf(" ");
        final int indexOfLastCh = spaceIndex == -1 ? length : spaceIndex;

        final String command = startCommandLine
                .substring(startCommandLine.indexOf("#") + 1, indexOfLastCh)
                .toLowerCase()
                .trim();

        if (StringUtils.isBlank(command)) return null;

        if (command.contains(Command.FLOOR.getValue()) && command.contains(Command.SECTION.getValue())) {
            return new CommandAndCommandLine(Command.SECTIONFLOORMIX, command);
        }

        final Command privateCommand = Command.findByContainingValue(command);
        if (privateCommand == null) return null;

        return new CommandAndCommandLine(privateCommand, command);
    }
}

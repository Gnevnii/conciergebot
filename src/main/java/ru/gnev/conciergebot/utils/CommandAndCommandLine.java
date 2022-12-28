package ru.gnev.conciergebot.utils;

import ru.gnev.conciergebot.bean.Command;

public record CommandAndCommandLine(Command command, String commandLine) {
}

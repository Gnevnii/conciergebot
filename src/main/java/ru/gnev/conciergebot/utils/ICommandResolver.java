package ru.gnev.conciergebot.utils;

public interface ICommandResolver {

    CommandAndCommandLine resolveCommandInfo(String message);
}

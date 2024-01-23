package ru.gnev.conciergebot;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.DefaultSender;
import org.telegram.telegrambots.extensions.bots.timedbot.TimedSendLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Error;
import ru.gnev.conciergebot.config.BotConfiguration;
import ru.gnev.conciergebot.persist.repository.ErrorRepositoryImpl;
import ru.gnev.conciergebot.reactions.ReactionRegistry;
import ru.gnev.conciergebot.reactions.eventreactions.BotAddedToChatReaction;
import ru.gnev.conciergebot.reactions.eventreactions.IReact;
import ru.gnev.conciergebot.reactions.eventreactions.UpdateUserInfoReaction;
import ru.gnev.conciergebot.reactions.eventreactions.UserRemovedFromChatReaction;
import ru.gnev.conciergebot.reactions.eventreactions.withanswerkeyboard.UserRegistrationWKeyboardReaction;
import ru.gnev.conciergebot.utils.CommandAndCommandLine;
import ru.gnev.conciergebot.utils.ICommandResolver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Optional;

public class ConciergeBot extends TimedSendLongPollingBot {
    private static final Logger LOGGER = LogManager.getLogger(ConciergeBot.class);

    private final ICommandResolver commandResolver;
    private final BotConfiguration botConfiguration;
    private final ReactionRegistry reactionRegistry;
    private final ErrorRepositoryImpl errorRepository;

    public ConciergeBot(ICommandResolver commandResolver,
                        BotConfiguration botConfiguration,
                        ReactionRegistry reactionRegistry,
                        ErrorRepositoryImpl errorRepository) {
        this.commandResolver = commandResolver;
        this.botConfiguration = botConfiguration;
        this.reactionRegistry = reactionRegistry;
        this.errorRepository = errorRepository;
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }

    @Override
    public void onUpdateReceived(final Update update) {
        try {
            //сохранить данные о чате, в который добавили бота
            Optional<IReact> optional = reactionRegistry.getReaction(BotAddedToChatReaction.class);
            if (optional.isPresent()) {
                final IReact iReact = optional.get();
                if (iReact.matchPolicy(update) && iReact.isValid(update)) {
                    iReact.react(update, this::sendTimed, "");
                    return;
                }
            }

            //сохранить/обновить данные о пользователе
            optional = reactionRegistry.getReaction(UpdateUserInfoReaction.class);
            if (optional.isPresent()) {
                final IReact iReact = optional.get();
                if (iReact.matchPolicy(update) && iReact.isValid(update))
                    iReact.react(update, this::sendTimed, "");
            }

            //если пользователя удаляют - пометить его удаленным
            optional = reactionRegistry.getReaction(UserRemovedFromChatReaction.class);
            if (optional.isPresent()) {
                final IReact iReact = optional.get();
                if (iReact.matchPolicy(update) && iReact.isValid(update)) {
                    iReact.react(update, this::sendTimed, "");
                    return;
                }
            }

            //регистрация пользователя
            optional = reactionRegistry.getReaction(UserRegistrationWKeyboardReaction.class);
            if (optional.isPresent()) {
                final IReact iReact = optional.get();
                if (iReact.matchPolicy(update) && iReact.isValid(update)) {
                    iReact.react(update, this::sendTimed, "");
                    return;
                }
            }

            //команды бота работают с сообщениями или с фото. если такого нет - то и смысла нет перебирать все команды
            if (!isSupportedByBotMessage(update)) {
                return;
            }

            //команды
            reactionRegistry.getCommands().stream()
                    .filter(command -> command.isValid(update))
                    .filter(command -> command.matchPolicy(update))
                    .forEach(command -> {
                        final CommandAndCommandLine commandAndCommandLine = commandResolver.resolveCommandInfo(update.getMessage().getText());
                        if (commandAndCommandLine == null) {
                            if (command.getCommand() == Command.PHOTO_SENT && update.getMessage().hasPhoto()) {
                                command.react(update, this::sendTimed, "");
                            }
                            return;
                        }

                        if (command.getCommand() == commandAndCommandLine.command())
                            command.react(update, this::sendTimed, commandAndCommandLine.commandLine());
                    });
        } catch (Throwable throwable) {
            LOGGER.error(throwable);

            if (update.hasMessage()) {
                final Error error = new Error();
                final Message message = update.getMessage();
                final User from = message.getFrom();
                if (from != null) {
                    error.setTgUserId(from.getId());
                    error.setTgGroupChatId(message.getChatId());
                    error.setErrorDateTime(new Date());
                    error.setMessageText(message.getText());

                    String exceptionAsString = getStringException(throwable);
                    error.setErrorText(exceptionAsString);
                    errorRepository.save(error);
                }
            }
        }
    }

    private String getStringException(final Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private boolean isSupportedByBotMessage(Update update) {
        if (update == null)
            return false;

        final Message message = update.getMessage();
        if (message == null)
            return false;

        return (StringUtils.isNotBlank(message.getText()) && message.getText().contains("#")) || message.hasPhoto();
    }

    @Override
    public void sendMessageCallback(Long chatId, Object messageRequest) {
        final DefaultSender defaultSender = new DefaultSender(this);
        try {
            if (messageRequest instanceof SendMessage) {
                defaultSender.execute((SendMessage) messageRequest);
            } else if (messageRequest instanceof ForwardMessage) {
                defaultSender.execute((ForwardMessage) messageRequest);
            } else if (messageRequest instanceof EditMessageReplyMarkup) {
                defaultSender.execute((EditMessageReplyMarkup) messageRequest);
            } else {
                throw new RuntimeException("No casting for type: " + messageRequest.getClass().getSimpleName());
            }
        } catch (Exception e) {
            final Error error = new Error();
            error.setTgGroupChatId(chatId);
            error.setErrorText(getStringException(e));
            errorRepository.save(error);
        }
    }
}

package ru.gnev.conciergebot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberMember;
import ru.gnev.conciergebot.command.CommandContainer;
import ru.gnev.conciergebot.command.CommandName;
import ru.gnev.conciergebot.service.SendMessageServiceImpl;

@Component
public class ConciergeBot extends TelegramLongPollingBot {
    public static String COMMAND_PREFIX = "/";
    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;

    private final CommandContainer commandContainer;

    public ConciergeBot() {
        this.commandContainer = new CommandContainer(new SendMessageServiceImpl(this));
    }

    @Override
    public void onUpdateReceived(final Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText().trim();
                if (messageText.startsWith(COMMAND_PREFIX)) {
                    String commandIdentifier = messageText.split(" ")[0].toLowerCase();
                    commandContainer.retrieveCommand(commandIdentifier).execute(update);
                } else {
                    commandContainer.retrieveCommand(CommandName.NO.getCommandName()).execute(update);
                }
            }
            return;
        }

        if (isFistTimeAddToChatUpdate(update)) {

        }
    }

    private boolean isFistTimeAddToChatUpdate(Update update) {
        if (update == null) return false;

        ChatMemberUpdated myChatMember = update.getMyChatMember();
        if (myChatMember == null) return false;

        ChatMember newChatMember = myChatMember.getNewChatMember();
        if (newChatMember == null) return false;

        if (ChatMemberMember.STATUS.equals(newChatMember.getStatus())) {
            if (newChatMember.getUser().getIsBot() && newChatMember.getUser().getUserName().equals(getBotUsername())) {
                Chat chat = myChatMember.getChat();
                Long id = chat.getId();

                String chatUserName = chat.getUserName();
                String chetTitle = chat.getTitle();

                User from = myChatMember.getFrom();
                Long userAddedBot = from.getId();
                String userNameAddedBot = from.getUserName();
                Integer whenAddedBot = myChatMember.getDate();
            }
        }

        // TODO:
        return false;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

}

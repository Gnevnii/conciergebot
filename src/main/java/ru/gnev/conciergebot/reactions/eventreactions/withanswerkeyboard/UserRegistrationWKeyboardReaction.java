package ru.gnev.conciergebot.reactions.eventreactions.withanswerkeyboard;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.reactions.eventreactions.AbstractPrivateChatReaction;
import ru.gnev.conciergebot.service.IRegistrationService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Реакция строго для личного чата с ботом.
 * 1. Пользователь сначала полурегистрируется в групповом чате - (это даст понимание, что он состоит в целевом групповом чате).
 * 2. Далее пользователь может регистрироваться в личном чате у бота.
 */
@Component
public class UserRegistrationWKeyboardReaction extends AbstractPrivateChatReaction {
    private final IRegistrationService registrationService;

    @Autowired
    public UserRegistrationWKeyboardReaction(final IRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public Policy getPolicy() {
        return Policy.PRIVATE_CHAT;
    }

    @Override
    public boolean isValid(final Update update) {
        if (update == null || !update.hasMessage()) return false;

        final Message message = update.getMessage();
        return !registrationService.isUserRegistered(message.getFrom().getId());
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        final boolean userPartitionRegistered = registrationService.isUserPartitionRegistered(message.getFrom().getId(), message.getChatId());
        if (!userPartitionRegistered) {
            sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), "Не видел Вас в чате дома. Вы там что-нибудь когда-нибудь писали? Напишите Ы"));
            return;
        }

        final String nextQuestionText = registrationService.processRegistration(message.getFrom().getId(), message);
        if (StringUtils.isBlank(nextQuestionText)) return;

        sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), nextQuestionText));
    }

    @Override
    public Command getCommand() {
        return Command.REACTION;
    }

    @NotNull
    private static InlineKeyboardMarkup getMarkup() {
        final List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Да");
        inlineKeyboardButton.setCallbackData("#answer:Да");
        row.add(inlineKeyboardButton);
        rows.add(row);

        row = new ArrayList<>();
        inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Нет");
        inlineKeyboardButton.setCallbackData("#answer:Нет");
        row.add(inlineKeyboardButton);
        rows.add(row);
        return inlineKeyboardMarkup;
    }

    @NotNull
    private ReplyKeyboardMarkup getReplyMarkup() {
        final ArrayList<KeyboardRow> objects = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Да"));
        objects.add(keyboardRow);

        keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Нет"));
        objects.add(keyboardRow);

        return new ReplyKeyboardMarkup(objects, false, true, false, "Выберете ответе");
    }
}

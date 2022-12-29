package ru.gnev.conciergebot.reactions.eventreactions.withanswerkeyboard;

import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.gnev.conciergebot.bean.AnswerButtonConfig;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.QuestionKeyboardConfig;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.bean.entity.RegQuestionType;
import ru.gnev.conciergebot.bean.entity.User;
import ru.gnev.conciergebot.bean.entity.registration.RegistrationQuestion;
import ru.gnev.conciergebot.bean.entity.registration.UserRegistrationAnswer;
import ru.gnev.conciergebot.persist.repository.UserRegistrationAnswerRepository;
import ru.gnev.conciergebot.persist.repository.UserRepository;
import ru.gnev.conciergebot.reactions.eventreactions.AbstractPrivateChatReaction;
import ru.gnev.conciergebot.service.IRegistrationService;
import ru.gnev.conciergebot.utils.CallbackQueryDataHelper;
import ru.gnev.conciergebot.utils.CallbackQueryParsed;
import ru.gnev.conciergebot.utils.FlatHelper;
import ru.gnev.conciergebot.utils.SectionFloor;

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
    private final FlatHelper flatHelper;
    private final UserRepository userRepository;
    private final UserRegistrationAnswerRepository userRegistrationAnswerRepository;
    private final CallbackQueryDataHelper callbackQueryDataHelper;

    @Autowired
    public UserRegistrationWKeyboardReaction(final IRegistrationService registrationService,
                                             final FlatHelper flatHelper,
                                             final UserRepository userRepository,
                                             final UserRegistrationAnswerRepository userRegistrationAnswerRepository,
                                             final CallbackQueryDataHelper callbackQueryDataHelper) {
        this.registrationService = registrationService;
        this.flatHelper = flatHelper;
        this.userRepository = userRepository;
        this.userRegistrationAnswerRepository = userRegistrationAnswerRepository;
        this.callbackQueryDataHelper = callbackQueryDataHelper;
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
    @Transactional
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

    @Transactional
    public void processMessageText(final String text,
                                   final Long chatId,
                                   final Long tgUserId,
                                   final BiConsumer<Long, Object> sender) {
        final User user = userRepository.getUserByTgUserId(tgUserId);
        final RegistrationQuestion question = registrationService.getNextRegistrationQuestion(user);
        if (question == null) return;

        final RegQuestionType flatCoordinate = RegQuestionType.findByMeaning(question.getQuestionMeaning());
        if (flatCoordinate == null) return;

        boolean isSend = switch (flatCoordinate) {
            case NAME -> {
                user.setName(text.trim());
                userRepository.save(user);
                yield false;
            }
            case ADDRESS -> {
                final boolean isValid = isValidBooleanAnswer(text.trim());
                if (!isValid) {
                    sender.accept(chatId, new SendMessage(String.valueOf(chatId), "Некорректный ответ"));
                    yield true;
                }
                yield false;
            }
            case FLOOR -> {
                final boolean isValid = isValidFloor(text.trim());
                if (!isValid) {
                    sender.accept(chatId, new SendMessage(String.valueOf(chatId), "Некорректный номер этажа"));
                    yield true;
                }
                yield false;
            }
            case SECTION -> {
                final boolean isValid = isValidSection(text.trim());
                if (!isValid) {
                    sender.accept(chatId, new SendMessage(String.valueOf(chatId), "Некорректный номер плана"));
                    yield true;
                }
                yield false;
            }
            case FLAT -> {
                final boolean isValid = isValidFlatLimit(text.trim());
                if (!isValid) {
                    sender.accept(chatId, new SendMessage(String.valueOf(chatId), "Некорректный номер квартиры"));
                    yield true;
                }

                calculateAndSaveFloorSection(user, text.trim());
                yield false;
            }
        };

        if (!isSend) {
            saveAnswer(text, user, question);
        }
    }

    @Transactional
    public void calculateAndSaveFloorSection(final User user, final String flat) {
        final SectionFloor sectionFloor = flatHelper.calculateSectionFloor(Integer.parseInt(flat));
        user.setSectionNumber(sectionFloor.section());
        user.setFloorNumber(sectionFloor.floor());
        user.setFlatNumber(Integer.parseInt(flat));
        userRepository.save(user);
    }

    private boolean isValidBooleanAnswer(final String text) {
        return text.equalsIgnoreCase("да") || text.equalsIgnoreCase("нет");
    }

    @Transactional
    public void saveAnswer(final String text, final User user, final RegistrationQuestion question) {
        final UserRegistrationAnswer answer = new UserRegistrationAnswer();
        answer.setTgUserId(user);
        answer.setQuestion(question);
        answer.setAnswer(text.trim());
        userRegistrationAnswerRepository.save(answer);
    }

    private boolean isMatchUserFloorSection(final String text, long tgUserId) {
        final boolean creatable = NumberUtils.isCreatable(text);
        if (!creatable) return false;

        int flat = NumberUtils.createInteger(text);
        final User user = userRepository.getUserByTgUserId(tgUserId);
        final SectionFloor sectionFloor = flatHelper.calculateSectionFloor(flat);
        return sectionFloor.floor() == user.getFloorNumber() && sectionFloor.section() == user.getSectionNumber();
    }

    private boolean isValidSection(final String text) {
        final boolean isCreatable = NumberUtils.isCreatable(text);
        if (!isCreatable) return false;

        return Integer.parseInt(text) >= 1 && Integer.parseInt(text) <= 8;
    }

    private boolean isValidFloor(final String text) {
        final boolean isCreatable = NumberUtils.isCreatable(text);
        if (!isCreatable) return false;

        return Integer.parseInt(text) >= 2 && Integer.parseInt(text) <= 25;
    }

    private boolean isValidFlatLimit(final String text) {
        final boolean isCreatable = NumberUtils.isCreatable(text);
        if (!isCreatable) return false;

        return Integer.parseInt(text) >= 1 && Integer.parseInt(text) <= 192;
    }

    private void sendNextQuestion(long chatId, long tgUserid, final BiConsumer<Long, Object> sender) {
        final QuestionKeyboardConfig question = registrationService.getNextRegistrationQuestionConfig(tgUserid);
        if (question == null) {
            sender.accept(chatId, new SendMessage(String.valueOf(chatId), "Вы ответили на все необходимые вопросы! Вам доступны функции бота"));
            return;
        }

        final SendMessage sendMessage = new SendMessage(String.valueOf(chatId), question.getQuestion());
        if (question.getButtonConfigs().isEmpty()) {
            //нет вариантов ответа, т.е. открытый вопрос - пользователь должен ввести строковый ответ
            sender.accept(chatId, sendMessage);
            return;
        }

        sendMessage.setReplyMarkup(buildKeyboard(question));
        sender.accept(chatId, sendMessage);
    }

    @NotNull
    private InlineKeyboardMarkup buildKeyboard(final QuestionKeyboardConfig question) {
        final List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        final InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(rows);

        final List<AnswerButtonConfig> buttonConfigs = question.getButtonConfigs();
        for (int i = 0; i < buttonConfigs.size(); i++) {
            if (i % 5 == 0) {
                //не будем выдавать больше 5 кнопок в ряд
                rows.add(row);
                row = new ArrayList<>();
            }
            final AnswerButtonConfig buttonConfig = buttonConfigs.get(i);
            final InlineKeyboardButton button = new InlineKeyboardButton(buttonConfig.getLabel());
            button.setCallbackData(buttonConfig.getCallbackData());
            row.add(button);
        }

        if (!row.isEmpty()) rows.add(row);
        if (!rows.isEmpty()) inlineKeyboard.setKeyboard(rows);
        return inlineKeyboard;
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

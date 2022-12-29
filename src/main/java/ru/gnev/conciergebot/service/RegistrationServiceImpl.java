package ru.gnev.conciergebot.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.QuestionKeyboardConfig;
import ru.gnev.conciergebot.bean.entity.Building;
import ru.gnev.conciergebot.bean.entity.User;
import ru.gnev.conciergebot.bean.entity.registration.RegistrationAnswerTemplate;
import ru.gnev.conciergebot.bean.entity.registration.RegistrationQuestion;
import ru.gnev.conciergebot.bean.entity.registration.UserRegistrationAnswer;
import ru.gnev.conciergebot.persist.repository.BuildingRepositoryImpl;
import ru.gnev.conciergebot.persist.repository.RegistrationQuestionRepository;
import ru.gnev.conciergebot.persist.repository.UserRegistrationAnswerRepository;
import ru.gnev.conciergebot.persist.repository.UserRepository;
import ru.gnev.conciergebot.utils.CallbackQueryDataHelper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationServiceImpl implements IRegistrationService {

    private final RegistrationQuestionRepository questionRepository;
    private final UserRegistrationAnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final BuildingRepositoryImpl buildingRepository;

    @Inject
    public RegistrationServiceImpl(RegistrationQuestionRepository questionRepository,
                                   UserRegistrationAnswerRepository answerRepository,
                                   UserRepository userRepository,
                                   final BuildingRepositoryImpl buildingRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
    }


    /**
     * Последовательная обработка регистрации пользователя у бота.
     * Пользователь должен ответить на строгую последовательность обязательных вопросов
     * Алгоритм:
     * - проверить ответил ли пользователь хотя бы на один вопрос. Если нет - получаем первый вопрос, сохраняем его для пользователя и возвращаем текст вопроса
     * -    если да - получаем последнюю запись пользовательских ответов с незаполненным текстом ответа.
     * - проверяем валидность текущего сообщения в качестве текста ответа
     * -    если невалиден - возвращаем текст последнего вопроса
     * -    если валиден - сохраняем значение
     * - проверяем есть ли еще вопросы для пользователя
     * -   если есть - ищем следующий вопрос и возвращаем его текст
     * -   если нет - сообщаем пользователю, что регистрация успешно пройдена
     */
    @Transactional
    @Override
    public String processRegistration(long tgUserId, Message message) {
        if (isUserRegistered(tgUserId)) {
            return "";
        }

        final User user = userRepository.getUserByTgUserId(tgUserId);
        final int userQuestionAnswers = answerRepository.countByTgUserId(user);
        if (userQuestionAnswers == 0) {
            //если не было ответов ни на один вопрос, то message обрабатывать не нужно, сразу запускаем вопрос;
            final RegistrationQuestion question = questionRepository.getRegistrationQuestionByQuestionOrder(1);
            final UserRegistrationAnswer answer = new UserRegistrationAnswer();
            answer.setTgUserId(userRepository.getUserByTgUserId(tgUserId));
            answer.setQuestion(question);
            answerRepository.save(answer);

            return question.getQuestionText();
        }

        String s = hasUnacceptableAnswers(tgUserId);
        if (StringUtils.isNotBlank(s)) {
            return s;
        }

        final List<UserRegistrationAnswer> answers = answerRepository.findAllByTgUserId(user);
        UserRegistrationAnswer unfinished = answers.stream().filter(a -> a.getAnswer() == null || StringUtils.isBlank(a.getAnswer())).findFirst().get();
        if (!validAnswerTypeForQuestion(message.getText(), unfinished.getQuestion().getAnswerType())) {
            return "Неверный формат ответа. " + unfinished.getQuestion().getQuestionText();
        }

        if (!isValidValue(message.getText(), unfinished)) {
            return "Некорректный ответ. " + unfinished.getQuestion().getQuestionText();
        }

        unfinished.setAnswer(message.getText().trim().toLowerCase());
        answerRepository.save(unfinished);
        syncToUser(tgUserId, unfinished);

        s = hasUnacceptableAnswers(tgUserId);
        if (StringUtils.isNotBlank(s)) {
            return s;
        }

        final int questionTotalCount = (int) questionRepository.count();
        if (userQuestionAnswers == questionTotalCount) {
            return "Вы ответили на все обязательные вопросы. У вас есть доступ к командам бота.";
        }

        final RegistrationQuestion question = questionRepository.getRegistrationQuestionByQuestionOrder(userQuestionAnswers + 1);
        final UserRegistrationAnswer answer = new UserRegistrationAnswer();
        answer.setTgUserId(userRepository.getUserByTgUserId(tgUserId));
        answer.setQuestion(question);
        answerRepository.save(answer);
        return question.getQuestionText();
    }

    @Override
    public QuestionKeyboardConfig getNextRegistrationQuestionConfig(long tgUserId) {
        if (isUserRegistered(tgUserId)) {
            return null;
        }

        final User user = userRepository.getUserByTgUserId(tgUserId);
        final RegistrationQuestion question = getNextRegistrationQuestion(user);
        if (question == null) return null;

        final QuestionKeyboardConfig config = new QuestionKeyboardConfig(question.getQuestionText());
        final List<RegistrationAnswerTemplate> answers = regAnswerRep.findAllByQuestion(question);
        if (!answers.isEmpty()) {
            config.setButtonConfigs(answers
                    .stream()
                    .map(answer -> new AnswerButtonConfig(answer.getLabel(), callbackQueryDataHelper.createCallbackQueryData(tgUserId, question.getId(), answer.getLabel())))
                    .toList());
        }
        return config;
    }

    @Override
    public RegistrationQuestion getNextRegistrationQuestion(final User user) {
        final int questionAnsweredCount = userRegistrationAnswerRep.countByTgUserId(user);
        return questionRep.getRegistrationQuestionByQuestionOrder(questionAnsweredCount + 1);
    }

    @Override
    public boolean isKnownUser(final long tgUserId, final long tgChatId) {
        final User user = userRepository.getUserByTgUserId(tgUserId);
        return user != null;
    }

    @Override
    public String hasUnacceptableAnswers(long tgUserId) {
        final List<UserRegistrationAnswer> allByTgUserId = answerRepository.findAllByTgUserId(userRepository.getUserByTgUserId(tgUserId));
        final Optional<UserRegistrationAnswer> optional = allByTgUserId.stream().filter(q -> q.getQuestion().getQuestionOrder() == 1).findFirst();

        if (optional.isPresent()
                && optional.get().getAnswer() != null
                && !optional.get().getAnswer().trim().equalsIgnoreCase("да")
                && !optional.get().getAnswer().isBlank()) {
            return "Вы проживаете по адресу, который не поддерживается этим ботом";
        }
        return null;
    }

    private void syncToUser(long tgUserId, final UserRegistrationAnswer answer) {
        final Command p = Command.findByValue(answer.getQuestion().getQuestionMeaning());

        final User user = userRepository.getUserByTgUserId(tgUserId);
        if (p == Command.FLOOR) {
            user.setFloorNumber(Integer.parseInt(answer.getAnswer()));
        } else if (p == Command.SECTION) {
            user.setSectionNumber(Integer.parseInt(answer.getAnswer()));
        }
        userRepository.save(user);
    }

    private boolean isValidValue(final String text, final UserRegistrationAnswer answer) {
        final Command p = Command.findByValue(answer.getQuestion().getQuestionMeaning());
        if (p == null) return false;

        return switch (p) {
            case FLOOR -> Integer.parseInt(text) >= 2 && Integer.parseInt(text) <= 25;
            case SECTION -> Integer.parseInt(text) >= 1 && Integer.parseInt(text) <= 8;
            case ADDRESS -> text.trim().equalsIgnoreCase("да") || text.trim().equalsIgnoreCase("нет");
            default -> false;
        };
    }

    private boolean validAnswerTypeForQuestion(final String text, final UserRegistrationAnswer answer) {
        final Command p = Command.findByValue(answer.getQuestion().getQuestionMeaning());
        if (p == null) return false;

        return switch (p) {
            case FLOOR -> NumberUtils.isCreatable(text);
            case SECTION -> NumberUtils.isCreatable(text);
            case ADDRESS -> text.trim().equalsIgnoreCase("да") || text.trim().equalsIgnoreCase("нет");
            default -> false;
        };
    }

    @Override
    public boolean isUserRegistered(final long tgUserId) {
        // TODO: есть еще вариант проверки ответов на все вопросы при регистрации. можно совместить
        final List<UserRegistrationAnswer> allByTgUserId = answerRepository.findAllByTgUserId(userRepository.getUserByTgUserId(tgUserId));


        final User user = userRepository.getUserByTgUserId(tgUserId);
        if (user == null) {
            return false;
        }

        return user.getSectionNumber() != 0 && user.getFloorNumber() != 0;
    }

    @Override
    @Transactional
    public void registerBotInChat(final Long tgUserId,
                                  final Long chatId) {
        final Building building = new Building();
        building.setBuildingNum("8 к.2");
        building.setStreet("Саларьевская");
        building.setFloorMax(25);
        building.setSectionMax(8);
        building.setTgGroupChatId(chatId);
        building.setTgUserAddedBot(tgUserId);
        building.setBotDeleted(false);
        building.setCreationDate(new Date());
        buildingRepository.save(building);
    }

    @Override
    public boolean isBotRegistered(final Long chatId) {
        return buildingRepository.existsByTgGroupChatId(chatId);
    }

    @Override
    public boolean isFromAddress(final Long tgUserId) {
        final List<UserRegistrationAnswer> answers = userRegistrationAnswerRep.getByTgUserId(userRepository.getUserByTgUserId(tgUserId));
        if (answers.isEmpty()) return true; //еще нет данных, но потенциально может быть - да

        final Optional<UserRegistrationAnswer> optional = answers
                .stream()
                .filter(a -> a.getQuestion().getQuestionOrder() == 1)
                .findFirst();
        return optional.map(answer -> answer.getAnswer().equalsIgnoreCase("да")).orElse(false);
    }
}

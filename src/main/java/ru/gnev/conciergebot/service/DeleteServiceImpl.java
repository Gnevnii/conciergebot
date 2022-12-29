package ru.gnev.conciergebot.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gnev.conciergebot.persist.repository.UserRegistrationAnswerRepository;
import ru.gnev.conciergebot.persist.repository.UserRepository;

@Service
public class DeleteServiceImpl implements DeleteService {
    private static final Logger LOGGER = LogManager.getLogger(DeleteServiceImpl.class);
    private final UserRepository userRepository;
    private final UserRegistrationAnswerRepository answerRepository;

    public DeleteServiceImpl(final UserRepository userRepository,
                             final UserRegistrationAnswerRepository answerRepository) {
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    @Transactional(readOnly = false)
    public void markDeleted(long tgUserId) {
        if (userRepository.existsByTgUserId(tgUserId)) {
            userRepository.markDeleted(tgUserId);
            answerRepository.deleteUserRegistrationAnswersByTgUserId(userRepository.getUserByTgUserId(tgUserId));
            LOGGER.debug(() -> String.format("User marked as deleted. telegram id: %d", tgUserId));
        }
    }
}

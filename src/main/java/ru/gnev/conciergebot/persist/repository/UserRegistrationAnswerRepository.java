package ru.gnev.conciergebot.persist.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gnev.conciergebot.bean.entity.User;
import ru.gnev.conciergebot.bean.entity.registration.UserRegistrationAnswer;

import java.util.List;

@Repository
public interface UserRegistrationAnswerRepository extends CrudRepository<UserRegistrationAnswer, Long> {

    int countByTgUserId(final User tgUserId);

    List<UserRegistrationAnswer> getByTgUserId(final User user);

    List<UserRegistrationAnswer> findAllByTgUserId(final User tgUserId);

    void deleteUserRegistrationAnswersByTgUserId(final User tgUserId);
}

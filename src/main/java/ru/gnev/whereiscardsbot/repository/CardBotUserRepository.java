package ru.gnev.whereiscardsbot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gnev.whereiscardsbot.bean.User;

@Repository
public interface CardBotUserRepository extends CrudRepository<User, String> {

    User getUserById(long userId);

    boolean existsUserByTgUserId(long tgUserId);
}

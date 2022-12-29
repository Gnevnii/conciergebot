package ru.gnev.conciergebot.persist.repository;

import org.springframework.data.repository.CrudRepository;
import ru.gnev.conciergebot.bean.entity.registration.RegistrationQuestion;

public interface RegQuestionRepository extends CrudRepository<RegistrationQuestion, Long> {

    RegistrationQuestion getRegistrationQuestionByQuestionOrder(int orderNumber);
}

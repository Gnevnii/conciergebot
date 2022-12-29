package ru.gnev.conciergebot.persist.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gnev.conciergebot.bean.entity.registration.RegistrationAnswerTemplate;
import ru.gnev.conciergebot.bean.entity.registration.RegistrationQuestion;

import java.util.List;

@Repository
public interface RegAnswerRepository extends CrudRepository<RegistrationAnswerTemplate, Long> {

    List<RegistrationAnswerTemplate> findAllByQuestion(RegistrationQuestion question);
}

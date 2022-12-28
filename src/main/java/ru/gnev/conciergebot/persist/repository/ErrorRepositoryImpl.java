package ru.gnev.conciergebot.persist.repository;

import org.springframework.data.repository.CrudRepository;
import ru.gnev.conciergebot.bean.entity.Error;

public interface ErrorRepositoryImpl extends CrudRepository<Error, Long> {
}

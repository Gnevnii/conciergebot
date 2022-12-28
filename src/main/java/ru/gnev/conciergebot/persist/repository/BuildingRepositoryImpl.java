package ru.gnev.conciergebot.persist.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gnev.conciergebot.bean.entity.Building;

@Repository
public interface BuildingRepositoryImpl extends CrudRepository<Building, Long> {

    boolean existsByTgGroupChatId(Long tgChatId);
}

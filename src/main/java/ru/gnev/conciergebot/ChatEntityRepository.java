package ru.gnev.conciergebot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatEntityRepository extends JpaRepository<ChatEntity, UUID> {
}
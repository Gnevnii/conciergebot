package ru.gnev.whereiscardsbot.bean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "wcb_possession", schema = "whereiscard_db")
public class Possession {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_guid", nullable = false)
    private long id;
    @Column(name = "l_card_id")
    private long cardId;
    @Column(name = "l_user_id")
    private String userId;
    @Column(name = "dt_start")
    private Date possesionStartDate;
}

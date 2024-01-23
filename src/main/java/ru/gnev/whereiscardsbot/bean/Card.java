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
@Table(name = "wcb_card", schema = "whereiscard_db")
public class Card {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_guid", nullable = false)
    private Long id;
    @Column(name = "i_card_code")
    private int cardCode;
    @Column(name = "l_owner_id")
    private String ownerId;
    @Column(name = "dt_in")
    private Date cardRegistered;
    @Column(name = "dt_out")
    private Date cardOuted;
}

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

@Getter
@Setter
@Entity
@Table(name = "wcb_user", schema = "whereiscard_db")
public class User {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_guid", nullable = false)
    private long id;
    @Column(name = "l_tg_user_id")
    private long tgUserId;
    @Column(name = "s_tg_user_name")
    private String tgUserName;
    @Column(name = "s_user_login")
    private String userLogin;
    @Column(name = "s_user_last_name")
    private String userLastName;
    @Column(name = "b_deleted")
    private boolean isDeleted;
}

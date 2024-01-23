package ru.gnev.conciergebot.bean.entity;

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
@Table(name = "cb_error", schema = "concierge_db")
public class Error {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_id", nullable = false)
    private Long id;
    @Column(name = "l_tg_groupchat_id")
    private long tgGroupChatId;
    @Column(name = "l_tg_user_id")
    private long tgUserId;
    @Column(name = "dt_error_date")
    private Date errorDateTime;
    @Column(name = "s_text", length = 2048)
    private String messageText;
    @Column(name = "s_error_text", length = 2048)
    private String errorText;
}

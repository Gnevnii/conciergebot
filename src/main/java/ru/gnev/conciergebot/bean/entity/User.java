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

@Getter
@Setter
@Entity
@Table(name = "cb_user", schema = "concierge_db")
public class User {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_id", nullable = false)
    private Long id;
    @Column(name = "l_tg_user_id")
    private long tgUserId;
    @Column(name = "s_tg_user_name")
    private String tgUserName;
    @Column(name = "l_tg_userbotchat_id")
    private long useBotChatId;
    @Column(name = "l_tg_groupchat_id")
    private long tgGroupChatId;
    @Column(name = "i_floor")
    private int floorNumber;
    @Column(name = "i_section")
    private int sectionNumber;
    @Column(name = "i_flat")
    private int flatNumber;
    @Column(name = "s_name")
    private String name;
    @Column(name = "b_deleted")
    private boolean isDeleted;
}

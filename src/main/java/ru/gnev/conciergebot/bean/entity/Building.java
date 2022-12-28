package ru.gnev.conciergebot.bean.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "cb_building", schema = "concierge_db")
public class Building {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_id", nullable = false)
    private Long id;
    @Column(name = "l_tg_groupchat_id")
    private long tgGroupChatId;
    @Column(name = "i_floor_max")
    private int floorMax;
    @Column(name = "i_section_max")
    private int sectionMax;
    @Column(name = "i_flat_max")
    private int flatMax;
    @Column(name = "s_street_name")
    private String street;
    @Column(name = "s_building_num")
    private String buildingNum;
    @Column(name = "l_tg_user_id_added_bot")
    private Long tgUserAddedBot;
    @Column(name = "b_deleted")
    private boolean isBotDeleted;
    @Column(name = "dt_creation_date")
    private Date creationDate;

    // TODO: нижняя граница
//    @Column(name = "i_floor_min")
//    private int floorMin;
//    @Column(name = "i_section_min")
//    private int sectionMin;
//    @Column(name = "i_flat_min")
//    private int flatMin;
}

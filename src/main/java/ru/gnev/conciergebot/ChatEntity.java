package ru.gnev.conciergebot;

import ru.gnev.conciergebot.bean.entity.ChatUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Alex,
 * 14.09.2022
 */
@Entity
@Table(name = "chat")
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ui_id", nullable = false)
    private UUID id;

    @Column(name = "i_tg_chat_id")
    private Long iTgChatId;

    @Column(name = "i_floor_count")
    private Short iFloorCount;

    @Column(name = "i_section_count")
    private Short iSectionCount;

    @Column(name = "i_entrance_number")
    private Short iEntranceNumber;

    @Size(max = 128)
    @Column(name = "s_creator_tg_user_name", length = 128)
    private String sCreatorTgUserName;

    @Column(name = "dt_creation_date")
    private Instant dtCreationDate;

    @OneToMany(mappedBy = "idChat")
    private Set<ChatUser> chatusers = new LinkedHashSet<>();

    public Set<ChatUser> getChatusers() {
        return chatusers;
    }

    public void setChatusers(Set<ChatUser> chatusers) {
        this.chatusers = chatusers;
    }

    public Instant getDtCreationDate() {
        return dtCreationDate;
    }

    public void setDtCreationDate(Instant dtCreationDate) {
        this.dtCreationDate = dtCreationDate;
    }

    public String getSCreatorTgUserName() {
        return sCreatorTgUserName;
    }

    public void setSCreatorTgUserName(String sCreatorTgUserName) {
        this.sCreatorTgUserName = sCreatorTgUserName;
    }

    public Short getIEntranceNumber() {
        return iEntranceNumber;
    }

    public void setIEntranceNumber(Short iEntranceNumber) {
        this.iEntranceNumber = iEntranceNumber;
    }

    public Short getISectionCount() {
        return iSectionCount;
    }

    public void setISectionCount(Short iSectionCount) {
        this.iSectionCount = iSectionCount;
    }

    public Short getIFloorCount() {
        return iFloorCount;
    }

    public void setIFloorCount(Short iFloorCount) {
        this.iFloorCount = iFloorCount;
    }

    public Long getITgChatId() {
        return iTgChatId;
    }

    public void setITgChatId(Long iTgChatId) {
        this.iTgChatId = iTgChatId;
    }

    public UUID getId() {
        return id;
    }
}

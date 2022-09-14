package ru.gnev.conciergebot.bean.entity;

import ru.gnev.conciergebot.ChatEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(name = "chatusers")
public class ChatUser {
    @Id
    @Column(name = "ui_id", nullable = false)
    private UUID id;

    @Size(max = 128)
    @Column(name = "s_tg_nickname", length = 128)
    private String sTgNickname;

    @NotNull
    @Column(name = "i_tg_user_id", nullable = false)
    private Long iTgUserId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chat", nullable = false)
    private ChatEntity idChat;

    @Column(name = "i_floor")
    private Integer iFloor;

    @Column(name = "i_section")
    private Integer iSection;

    @Column(name = "i_flat")
    private Integer iFlat;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSTgNickname() {
        return sTgNickname;
    }

    public void setSTgNickname(String sTgNickname) {
        this.sTgNickname = sTgNickname;
    }

    public Long getITgUserId() {
        return iTgUserId;
    }

    public void setITgUserId(Long iTgUserId) {
        this.iTgUserId = iTgUserId;
    }

    public ChatEntity getIdChat() {
        return idChat;
    }

    public void setIdChat(ChatEntity idChat) {
        this.idChat = idChat;
    }

    public Integer getIFloor() {
        return iFloor;
    }

    public void setIFloor(Integer iFloor) {
        this.iFloor = iFloor;
    }

    public Integer getISection() {
        return iSection;
    }

    public void setISection(Integer iSection) {
        this.iSection = iSection;
    }

    public Integer getIFlat() {
        return iFlat;
    }

    public void setIFlat(Integer iFlat) {
        this.iFlat = iFlat;
    }

}
package ru.gnev.conciergebot.bean.entity.registration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ru.gnev.conciergebot.bean.entity.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "cb_registration_answer", schema = "concierge_db")
public class UserRegistrationAnswer {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "l_tg_user_id")
    private User tgUserId;

    @ManyToOne
    @JoinColumn(name = "l_question_id")
    private RegistrationQuestion question;

    @Column(name = "s_answer")
    private String answer;
}

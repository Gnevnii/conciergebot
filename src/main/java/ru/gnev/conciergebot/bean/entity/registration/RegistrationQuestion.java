package ru.gnev.conciergebot.bean.entity.registration;

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
@Table(name = "cb_reg_question", schema = "concierge_db")
public class RegistrationQuestion {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_id", nullable = false)
    private Long id;

    @Column(name = "s_question_text")
    private String questionText;
    @Column(name = "s_question_meaning")
    private String questionMeaning;
    @Column(name = "i_question_order")
    private int questionOrder;
    @Column(name = "b_mandatory_question")
    private boolean isMandatory;
    @Column(name = "dt_effective_since")
    private Date effectiveSince;
}

package ru.gnev.conciergebot.bean.entity.registration;

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

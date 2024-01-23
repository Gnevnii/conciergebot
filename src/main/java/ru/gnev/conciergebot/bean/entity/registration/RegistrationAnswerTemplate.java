package ru.gnev.conciergebot.bean.entity.registration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "cb_reg_answer_template", schema = "concierge_db")
public class RegistrationAnswerTemplate {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_id", nullable = false)
    private Long id;
    @Column(name = "s_label")
    private String label;
    @Column(name = "s_type")
    private String type;
    @Column(name = "i_order")
    private int order;
    @ManyToOne
    @JoinColumn(name = "l_req_question_id")
    private RegistrationQuestion question;
}

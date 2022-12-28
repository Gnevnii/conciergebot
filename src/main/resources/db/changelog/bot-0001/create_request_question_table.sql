--liquibase formatted sql

--changeset gnev:3
create table concierge_db.cb_reg_question
(
    l_id                 bigserial,
    s_answer_type        varchar(255),
    dt_effective_since   timestamp,
    b_mandatory_question boolean,
    s_question_meaning   varchar(255),
    i_question_order     integer,
    s_question_text      varchar(255)
);

insert into concierge_db.cb_reg_question(dt_effective_since, s_answer_type, b_mandatory_question, s_question_meaning,
                                         i_question_order, s_question_text)
values (now(), 'boolean', true, 'адрес', 1, 'Вы проживаете по адреса ул. Саларьевская, д.8 к.2?'),
       (now(), 'integer', true, 'этаж', 2, 'На каком этаже вы живете?'),
       (now(), 'integer', true, 'план', 3, 'Какой номер плана у вашей квартиры?');
--liquibase formatted sql

--changeset gnev:3
create table concierge_db.cb_reg_question
(
    l_id                 bigserial,
    dt_effective_since   timestamp,
    b_mandatory_question boolean,
    s_question_meaning   varchar(255),
    i_question_order     integer,
    s_question_text      varchar(255)
);

insert into concierge_db.cb_reg_question(dt_effective_since, b_mandatory_question, s_question_meaning,
                                         i_question_order, s_question_text)
values (now(), true, 'адрес', 1, 'Вы проживаете по адреса ул. Саларьевская, д.8 к.2?'),
       (now(), true, 'квартира', 2, 'В какой квартире вы живете?'),
       (now(), true, 'имя', 3, 'Как вас зовут?');
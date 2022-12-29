--liquibase formatted sql

--changeset gnev:5
create table concierge_db.cb_registration_user_answer
(
    l_id          bigserial,
    s_answer      varchar(255),
    l_question_id bigint,
    l_tg_user_id  bigint
);
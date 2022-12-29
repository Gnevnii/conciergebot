--liquibase formatted sql

--changeset gnev:24
create table concierge_db.cb_reg_answer_template
(
    l_id                 bigserial,
    s_type               varchar(64),
    i_order              integer,
    s_label              varchar(255),
    l_req_question_id    bigint
);
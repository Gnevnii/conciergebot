--liquibase formatted sql

--changeset gnev:4
create table concierge_db.cb_error
(
    l_id              bigserial,
    l_tg_groupchat_id bigint,
    l_tg_user_id      bigint,
    dt_error_date     timestamp,
    s_text            varchar(512),
    s_error_text      varchar(2048)
);
--liquibase formatted sql

--changeset gnev:2
create table concierge_db.cb_user
(
    l_id                bigserial,
    i_flat              integer,
    i_floor             integer,
    s_name              varchar(255),
    i_section           integer,
    l_tg_groupchat_id   bigint,
    l_tg_user_id        bigint,
    s_tg_user_name      varchar(255),
    l_tg_userbotchat_id bigint,
    b_deleted           boolean
);
--liquibase formatted sql

--changeset gnev:1
create table concierge_db.cb_building
(
    l_id                   bigserial,
    s_building_num         varchar(255),
    i_flat_max             integer,
    i_floor_max            integer,
    i_section_max          integer,
    s_street_name          varchar(255),
    l_tg_groupchat_id      bigint,
    l_tg_user_id_added_bot bigint,
    b_deleted              boolean,
    dt_creation_date       timestamp
);
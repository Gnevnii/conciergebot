-- liquibase formatted sql

-- changeset gnev:1
CREATE TABLE IF NOT EXISTS chat
(
    ui_id                  uuid     DEFAULT uuid_generate_v4() primary key,
    i_tg_chat_id           bigint unique,
    i_floor_count          smallint default null,
    i_section_count        smallint default null,
    i_entrance_number      smallint default null,
    s_creator_tg_user_name varchar(128),
    dt_creation_date       timestamp
);

comment on table chat is 'Справочник чатов';
comment on column chat.ui_id is 'Внутренний идентификатор чата';
comment on column chat.i_tg_chat_id is 'Глобальный идентификатор чата';
comment on column chat.i_floor_count is 'Количество этажей';
comment on column chat.i_section_count is 'Количество планов';
comment on column chat.i_entrance_number is 'Номер подъезда';
comment on column chat.s_creator_tg_user_name is 'Имя пользователя в ТГ добавившего бота в чат';
comment on column chat.dt_creation_date is 'Дата добавления бота';

create index tg_id_indx on chat (i_tg_chat_id);
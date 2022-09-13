-- liquibase formatted sql

-- changeset gnev:2
CREATE TABLE IF NOT EXISTS chatusers
(
    ui_id         uuid DEFAULT uuid_generate_v4() primary key,
    s_tg_nickname varchar(128),
    i_tg_user_id  bigint                      NOT NULL,
    id_chat       uuid references chat(ui_id) NOT NULL,
    i_floor       INT,
    i_section     INT,
    i_flat        INT
);

comment on table chatusers is 'Пользователи чата';
comment on column chatusers.ui_id is 'Внутренний идентификатор пользователя';
comment on column chatusers.s_tg_nickname is 'Телеграм-ник пользователя';
comment on column chatusers.i_tg_user_id is 'Телеграм-идентификатор пользователя';
comment on column chatusers.id_chat is 'Внутренний идентификатор чата';
comment on column chatusers.i_floor is 'Этаж';
comment on column chatusers.i_section is 'Номер плана на этаже';
comment on column chatusers.i_flat is 'Номер квартиры';
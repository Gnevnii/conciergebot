-- liquibase formatted sql

-- changeset gnev:1
alter table chat add column i_floor_count int, add column i_section_count int;
comment on column chat.i_floor_count is 'Количество этажей';
comment on column chat.i_section_count is 'Количество планов на этаже';
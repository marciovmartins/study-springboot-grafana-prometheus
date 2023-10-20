create table fiis
(
    id             bigint unsigned primary key auto_increment,
    fii_name       varchar(45)            not null,
    current_value  decimal(6, 2) unsigned not null,
    fii_date       date                   not null,
    dividend_yield decimal(6, 2) unsigned null,
    created_at     timestamp              not null default current_timestamp,
    unique index fiis_date_uindex (fii_name, fii_date)
)
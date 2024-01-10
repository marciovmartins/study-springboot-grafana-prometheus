create table movements
(
    movement_id                   varbinary(16)                                           not null primary key,
    date                          date                                                    not null,
    type                          enum ('REVENUE', 'DIVIDENDS', 'TRANSFER', 'SETTLEMENT') not null,
    share_code                    varchar(10)                                             not null,
    brokerage                     varchar(255)                                            not null,
    quantity                      int unsigned                                            not null,
    unit_price_cent_amount        int unsigned                                            not null,
    transaction_value_cent_amount int unsigned                                            not null,
    currency                      varbinary(3)                                            not null,
    created_at                    timestamp(6) default CURRENT_TIMESTAMP(6)               not null
);

create index movements_share_code_i
    on movements (share_code, date);

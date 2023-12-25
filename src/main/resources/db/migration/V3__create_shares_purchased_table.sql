create table shares_purchased
(
    share_purchased_id      varbinary(16) not null primary key,
    share_code              varchar(10)   not null,
    date                    date          not null,
    share_price_cent_amount int unsigned  not null,
    share_price_currency    varbinary(3)  not null,
    quantity                int unsigned  not null,
    amount_paid_cent_amount int unsigned  not null,
    amount_paid_currency    varchar(3)    not null,
    created_at              timestamp(6)  not null default current_timestamp(6),
    index shares_purchased_share_code_i (`share_code`)
)
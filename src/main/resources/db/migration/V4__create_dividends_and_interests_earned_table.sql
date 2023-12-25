create table dividends_and_interests_earned
(
    dividends_and_interests_earned_id varbinary(16)   not null primary key,
    share_code                        varchar(10)  not null,
    date                              date         not null,
    amount_earned_cent_amount         int unsigned not null,
    amount_earned_currency            varchar(3)   not null,
    created_at                        timestamp(6) default current_timestamp(6) not null,
    index dividends_and_interests_earned_share_code_i (share_code)
)
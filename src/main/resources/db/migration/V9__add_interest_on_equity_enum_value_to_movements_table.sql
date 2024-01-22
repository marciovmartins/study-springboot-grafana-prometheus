alter table movements
    modify type enum ('REVENUE', 'DIVIDENDS', 'TRANSFER', 'SETTLEMENT', 'INTEREST_ON_EQUITY') not null;

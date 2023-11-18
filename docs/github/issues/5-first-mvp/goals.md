# Issue #5: Define the goals for the MVP

Date: 19.11.2023

Status: Accepted

## Context:

Figure out and define what could bring value to help the investor to decide what are the next Shares to be bought.

### Option 1: Profitability Share Options Report

The investor should be able to request the Share Options by profitability. Also, should be able to define the
period. For that, it is required to register all bought shares and provide the share code, the share price, the quantity
and the date that the share was bought.

The investor would need a report grouped by month, trimester, semester and annually.

Currently, the investor calculates the share profitability summing up all the share prices and calculating the
percentage of that in relation with the dividends and interests earned in the period.

For example, the bought shares:

| Share Code | Date       | Share Price | Qty | Paid    | Total Qty | Total Paid | 
|------------|------------|-------------|-----|---------|-----------|------------|
| RECT11     | 2023-10-20 | 31.89       | 1   | 31.89   | 169       | 15963,68   |
| RECT11     | 2023-05-22 | 62.24       | 77  | 4792.48 | 168       | 15931,79   |
| RECT11     | 2022-10-28 | 103.14      | 6   | 618.84  | 91        | 11139,31   |
| RECT11     | 2022-10-21 | 102.49      | 10  | 1024.90 | 85        | 10520,47   |
| RECT11     | 2022-07-27 | 112.80      | 1   | 112.80  | 75        | 9495,57    |

The dividends and interests earned according to B3 report:

| Share code | Date       | Amount Earned (R$) | Total Amount Earned (R$) | Profitability (%)  | Average Profitability (%) |
|------------|------------|--------------------|--------------------------|--------------------|---------------------------|
| RECT11     | 2023-11-16 | 57.46              | 2488.85                  | 0.3599420684954848 | 0.322327919443944         |
| RECT11     | 2023-10-16 | 45.36              | 2431.39                  | 0.2847137703924041 | 0.232240068441776         |
| RECT11     | 2023-09-15 | 28.64              | 2386.03                  | 0.1797663664911476 | 0.353507044720022         |
| RECT11     | 2023-08-16 | 84.00              | 2357.39                  | 0.5272477229488965 | 0.527247722948897         |
| RECT11     | 2023-07-14 | 84.00              | 2273.39                  | 0.5272477229488965 | 0.527247722948897         |

Profitability = ( Amount Earned * 100 ) / Total Paid
Average profitability is a moving average of profitability

### Option 2: Calculate when a share should be sold to minimize the loss

The investor would like to minimize the loss knowing when it is a good time to sell it. Taking in consideration the
current share price and the profitability

## Decision

The "Option 1: Profitability Share Options Report" is the best option to give most value since the investors
buys shares every month. Knowing which share should buy maximises the profit and this feature can be used immediately.

## Consequences

The MVP will output the result in a JSON format that will be consumed, in the future, by some frontend interface. The
bought shares will be inserted manually into the system/database and no api interface will be exposed for that at the
moment.

The investor will be able to request a monthly , trimester, semester or yearly report.
alter table group_purchase
    drop column initial_price,
    drop column current_participants,
    add column last_minimum_participants_met_date datetime(6) null comment '마지막 최소 인원 충족 일시';



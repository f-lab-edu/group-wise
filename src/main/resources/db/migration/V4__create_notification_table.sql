create table notification
(
    id                 bigint auto_increment
        primary key,
    member_id          bigint       not null comment '알림 수신자 ID',
    group_purchase_id  bigint       null comment '관련 공동구매 ID',
    title              varchar(255) not null comment '알림 제목',
    message            text         not null comment '알림 내용',
    notification_type  enum ('SUCCESS', 'FAILURE', 'MINIMUM_MET', 'MINIMUM_UNMET', 'START', 'CANCEL')
                                    not null comment '알림 타입',
    is_read            bit          not null default 0 comment '읽음 여부',
    delivered_channels varchar(255) null comment '전송된 채널 (SMS,EMAIL,APP)',
    created_date       datetime(6)  not null comment '알림 생성 일시',
    modified_date      datetime(6)  null comment '최종 수정 일시',
    constraint FK_notification__member__id
        foreign key (member_id) references member (id),
    constraint FK_notification__group_purchase__id
        foreign key (group_purchase_id) references group_purchase (id)
)
    comment '알림 메시지';
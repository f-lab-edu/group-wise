create table member
(
    id            bigint auto_increment
        primary key,
    username      varchar(255) null comment '이름',
    password      varchar(255) null comment '비밀번호',
    address       varchar(255) null comment '주소',
    created_date  datetime(6)  null comment '생성 일시',
    modified_date datetime(6)  null comment '최종 수정 일시'
)
    comment '회원';

create table category
(
    id            bigint auto_increment
        primary key,
    parent_id     bigint       null,
    name          varchar(255) not null comment '카테고리명',
    created_date  datetime(6)  null comment '생성 일시',
    modified_date datetime(6)  null comment '최종 수정 일시',
    constraint FK_category__category__id
        foreign key (parent_id) references category (id)
)
    comment '카테고리';

create table product
(
    id            bigint auto_increment
        primary key,
    seller        varchar(255)                             null comment '판매사',
    product_name  varchar(255)                             not null comment '상품명',
    base_price    int default 0                            not null comment '기본 가격',
    sale_status   enum ('DISCONTINUE', 'SALE', 'SOLD_OUT') null comment '판매 상태',
    created_date  datetime(6)                              null comment '생성 일시',
    modified_date datetime(6)                              null comment '최종 수정 일시'
)
    comment '상품 기본 정보';

create table product_category
(
    id            bigint auto_increment
        primary key,
    category_id   bigint      null,
    product_id    bigint      null,
    created_date  datetime(6) null comment '생성 일시',
    modified_date datetime(6) null comment '최종 수정 일시',
    constraint FK_product_category__product__id
        foreign key (product_id) references product (id),
    constraint FK_product_category__category__id
        foreign key (category_id) references category (id)
)
    comment '카테고리-상품 매핑';

create table product_attribute
(
    id             bigint auto_increment
        primary key,
    product_id     bigint       null comment '상품 id',
    attribute_name varchar(255) null comment '상품의 선택항목명 (ex: 색상, 모델명, 사이즈)',
    created_date   datetime(6)  null comment '생성 일시',
    modified_date  datetime(6)  null comment '최종 수정 일시',
    constraint FK_product_attribute__product__id
        foreign key (product_id) references product (id)
)
    comment '상품의 선택항목 (예: 색상, 사이즈)';

create table product_attribute_value
(
    id                   bigint auto_increment
        primary key,
    product_attribute_id bigint       null comment '상품의 선택항목 id',
    attribute_value_name varchar(255) null comment '상품의 선택항목에 대한 값 (ex: 선택항목이 색상이라면 빨간색이 값이 됨',
    additional_price     int          not null comment '상품(product)의 basePrice에 더하는 추가 금액',
    created_date         datetime(6)  null comment '생성 일시',
    modified_date        datetime(6)  null comment '최종 수정 일시',
    constraint FK_product_attribute_value__product_attribute__id
        foreign key (product_attribute_id) references product_attribute (id)
)
    comment '상품의 선택항목값 (예: 색상-빨강, 사이즈-L)';

create table product_stock
(
    id             bigint auto_increment
        primary key,
    product_id     bigint      null comment '상품 id',
    stock_quantity int         null comment '구매가능한 수량 (재고)',
    created_date   datetime(6) null comment '생성 일시',
    modified_date  datetime(6) null comment '최종 수정 일시',
    constraint FK_product_stock__product_id
        foreign key (product_id) references product (id)
)
    comment '상품의 최종 옵션 구성과 재고수량';

create table product_attribute_value_stock
(
    id                   bigint auto_increment
        primary key,
    product_stock_id     bigint      null comment '상품의 최종 옵션 구성 id',
    product_attribute_value_id   bigint      null comment '상품의 선택항목값 id',
    created_date         datetime(6) null comment '생성 일시',
    modified_date        datetime(6) null comment '최종 수정 일시',
    constraint FK_product_attribute_value_stock__product_attribute_value__id
        foreign key (product_attribute_value_id) references product_attribute_value (id),
    constraint FK_product_attribute_value_stock__product_stock__id
        foreign key (product_stock_id) references product_stock (id)
)
    comment '상품의 최종 옵션 구성과 선택항목값 매핑';

create table group_purchase
(
    id                   bigint auto_increment
        primary key,
    status               enum ('CANCELLED', 'COMPLETED_FAILURE', 'COMPLETED_SUCCESS', 'FULFILLED', 'ONGOING', 'PENDING') null comment '진행상태',
    title                varchar(255)                                                                                    null comment '공동구매 제목',
    product_id           bigint                                                                                          null comment '상품 id',
    discount_rate        int                                                                                             null comment '할인율',
    initial_price        int                                                                                             null comment '시작가격 (상품 기본가격)',
    minimum_participants int                                                                                             null comment '최소 참여 인원',
    current_participants int                                                                                             null comment '현재 참여자수',
    start_date           datetime(6)                                                                                     null comment '공동구매 시작일시',
    end_date             datetime(6)                                                                                     null comment '공동구매 마감일시',
    created_date         datetime(6)                                                                                     null comment '생성일시',
    modified_date        datetime(6)                                                                                     null comment '최종수정일시',
    constraint FK_group_purchase__product__id
        foreign key (product_id) references product (id)
)
    comment '공동구매 그룹';

create table group_purchase_participant
(
    id                bigint auto_increment
        primary key,
    group_purchase_id bigint      null comment '공동구매그룹 id',
    member_id         bigint      null comment '회원 id',
    product_stock_id  bigint      null comment '상품의 최종 옵션 구성  id',
    is_wishlist       bit         not null comment '회원의 관심여부',
    has_participated  bit         not null comment '회원의 구매참여 여부',
    quantity          int         null comment '회원의 구매수량',
    created_date      datetime(6) null comment '생성일시',
    modified_date     datetime(6) null comment '최종수정일시',
    constraint UK_group_purchase_participant__product_stock__id
        unique (product_stock_id),
    constraint FK_group_purchase_participant__product_stock__id
        foreign key (product_stock_id) references product_stock (id),
    constraint FK_group_purchase_participant__group_purchase__id
        foreign key (group_purchase_id) references group_purchase (id),
    constraint FK_group_purchase_participant__member__id
        foreign key (member_id) references member (id)
)
    comment '공동구매 참여자별 구매 정보';

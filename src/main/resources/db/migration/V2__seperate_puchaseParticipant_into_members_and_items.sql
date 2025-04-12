-- 기존 테이블의 외래키 제약 조건 제거
ALTER TABLE group_purchase_participant
    DROP FOREIGN KEY FK_group_purchase_participant__product_stock__id;

-- 기존 테이블 이름 변경
RENAME TABLE group_purchase_participant TO group_purchase_member;

-- 불필요한 컬럼 제거
ALTER TABLE group_purchase_member
    DROP COLUMN product_stock_id,
    DROP COLUMN quantity;

-- 새로운 테이블 생성
CREATE TABLE group_purchase_item
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_purchase_id BIGINT      NOT NULL,
    member_id         BIGINT      NOT NULL,
    product_stock_id  BIGINT      NOT NULL,
    quantity          INT         NOT NULL,
    created_date      DATETIME(6) NULL COMMENT '생성 일시',
    modified_date     DATETIME(6) NULL COMMENT '최종 수정 일시',
    CONSTRAINT FK_group_purchase_item__group_purchase__id FOREIGN KEY (group_purchase_id) REFERENCES group_purchase (id),
    CONSTRAINT FK_group_purchase_item__member__id FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT FK_group_purchase_item__product_stock__id FOREIGN KEY (product_stock_id) REFERENCES product_stock (id)
) COMMENT '공동구매 아이템 정보';

-- 구매 참여 데이터를 새 테이블로 이관
INSERT INTO group_purchase_item (group_purchase_id, member_id, product_stock_id, quantity, created_date, modified_date)
SELECT group_purchase_id, member_id, product_stock_id, quantity, created_date, modified_date
FROM group_purchase_participant
WHERE has_participated = true AND product_stock_id IS NOT NULL;
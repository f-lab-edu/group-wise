package wj.flab.group_wise.dto.groupPurchase.request;

import java.time.LocalDateTime;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.dto.SortDirection;

public record GroupPurchaseSearchRequest(
    // 기본 필터
    GroupPurchase.Status status,   // 공동구매 상태
    String title,                  // 게시물 제목 (like 검색)

    // 날짜 범위 필터
    LocalDateTime startDateFrom,   // 시작일 ≥
    LocalDateTime startDateTo,     // 시작일 ≤
    LocalDateTime endDateFrom,     // 종료일 ≥
    LocalDateTime endDateTo,       // 종료일 ≤

    // 가격 범위 필터
    Integer minPrice,              // 최소 가격
    Integer maxPrice,              // 최대 가격

    // 참여율 필터
    Double minParticipationRate,   // 최소 참여율 (0.0 ~ 1.0)

    // 정렬 옵션
    SortBy sortBy, // = Optional.of(SortBy.CREATED_DATE),  // 기본값 설정
    SortDirection sortDirection, // = Optional.of(SortDirection.DESC),

    int page, // = 0,
    int size // = 20
) {

    public enum SortBy {
        CREATED_DATE("생성일"),
        START_DATE("시작일"),
        END_DATE("종료일"),
        REMAINING_TIME("남은 시간"),
        PARTICIPATION_RATE("참여율"),
        CHEAPEST_PRICE("최저가"),
        PARTICIPANT_COUNT("참여자 수");

        private final String description;

        SortBy(String description) {
            this.description = description;
        }
    }



}

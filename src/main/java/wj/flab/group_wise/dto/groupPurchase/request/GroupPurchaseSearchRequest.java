package wj.flab.group_wise.dto.groupPurchase.request;

import java.time.LocalDateTime;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase.Status;
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
    Double minGoalAchievementRate,   // 최소 인원 목표 달성률 (0.0 ~ 1.0)

    // 정렬 옵션
    SortBy sortBy,
    SortDirection sortDirection,

    Integer page,
    Integer size
) {

    public GroupPurchaseSearchRequest(Status status, String title, LocalDateTime startDateFrom, LocalDateTime startDateTo, LocalDateTime endDateFrom, LocalDateTime endDateTo,
        Integer minPrice, Integer maxPrice, Double minGoalAchievementRate, SortBy sortBy, SortDirection sortDirection, Integer page, Integer size) {
        this.status = status;
        this.title = title;
        this.startDateFrom = startDateFrom;
        this.startDateTo = startDateTo;
        this.endDateFrom = endDateFrom;
        this.endDateTo = endDateTo;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minGoalAchievementRate = minGoalAchievementRate;
        this.sortBy = sortBy == null ? SortBy.CREATED_DATE : sortBy;
        this.sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;
        this.page = page == null ? 0 : page;
        this.size = size == null ? 20 : size;
    }

    public enum SortBy {
        CREATED_DATE("생성일"),
        START_DATE("시작일"),
        END_DATE("종료일"),
        REMAINING_TIME("남은 시간"),
        GOAL_ACHIEVEMENT_RATE("인원목표달성률"),
        CHEAPEST_PRICE("최저가"),
        PARTICIPANT_COUNT("참여자 수");

        private final String description;

        SortBy(String description) {
            this.description = description;
        }
    }



}

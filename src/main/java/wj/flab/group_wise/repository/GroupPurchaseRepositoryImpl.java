package wj.flab.group_wise.repository;


import static org.springframework.util.StringUtils.hasText;
import static wj.flab.group_wise.domain.groupPurchase.QGroupPurchase.groupPurchase;
import static wj.flab.group_wise.domain.groupPurchase.QGroupPurchaseMember.groupPurchaseMember;
import static wj.flab.group_wise.domain.product.QProduct.product;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import wj.flab.group_wise.domain.groupPurchase.GroupPurchase;
import wj.flab.group_wise.dto.groupPurchase.request.GroupPurchaseSearchRequest;


@Repository
public class GroupPurchaseRepositoryImpl implements GroupPurchaseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GroupPurchaseRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<GroupPurchase> searchGroupPurchases(GroupPurchaseSearchRequest searchRequest) {
        queryFactory
            .selectFrom(groupPurchase)
            .leftJoin(product).on(groupPurchase.productId.eq(product.id)).fetchJoin()
//            .leftJoin(product.productStocks, productStock).fetchJoin()
//            .leftJoin(productStock.values, valueStock).fetchJoin()
            .where(
                statusEq(searchRequest.status()),
                titleContain(searchRequest.title()),

                startDateGoe(searchRequest.startDateFrom()),
                startDateLoe(searchRequest.startDateTo()),
                endDateGoe(searchRequest.endDateFrom()),
                endDateLoe(searchRequest.endDateTo()),

//                disCountedPriceGoe(searchRequest.minPrice()),                // 최소 가격
//                disCountedPriceLoe(searchRequest.maxPrice()),                // 최대 가격
                participationRateGoe(searchRequest.minParticipationRate())   // 최소 참여율
            );

        return null;
    }

    private BooleanExpression participationRateGoe(Double minParticipationRate) {

        JPAQuery<Long> participantCountSubQuery = queryFactory
            .select(groupPurchaseMember.count()).from(groupPurchaseMember)
            .where(
                groupPurchaseMember.hasParticipated.isTrue(),
                groupPurchaseMember.groupPurchase.id.eq(groupPurchase.id)
            );

        // 참여율 = 실제 참여자 / 최소 참여자
        NumberExpression<Double> participationRate = Expressions.numberTemplate(Double.class,
            "({0} * 1.0) / {1}",
            participantCountSubQuery, groupPurchase.minimumParticipants);

        return minParticipationRate != null ? participationRate.goe(minParticipationRate) : null;
    }

//    private BooleanExpression disCountedPriceLoe(Integer maxPrice) {
//        NumberExpression<Integer> discountedPrice = getDiscountedPrice();
//        return maxPrice != null ? discountedPrice.loe(maxPrice) : null;
//    }
//
//    private BooleanExpression disCountedPriceGoe(Integer minPrice) {
//
//        NumberExpression<Integer> discountedPrice = getDiscountedPrice();
//
//        return discountedPrice.goe(minPrice);
//    }
//
//    /**
//     * 상품의 기본 가격과 구성옵션의 추가 가격을 합산한 후 할인율을 적용한 가격을 구하는 서브쿼리
//     * @return 할인된 가격
//     */
//    private NumberExpression<Integer> getDiscountedPrice() {
//        // GroupPurchase와 Product를 조인하고 있으므로, 기본 가격은 접근 가능
//        // ProductStock의 최소 가격을 추가해야 함
//
//        // 1단계: 각 상품 재고별 추가 가격의 합계를 구하는 서브쿼리
//        JPQLQuery<Integer> additionalPriceSum = JPAExpressions
//            .select(productAttributeValueStock.productAttributeValue.additionalPrice.sum())
//            .from(productAttributeValueStock)
//            .where(productAttributeValueStock.productStock.id.eq(productStock.id))
//            .groupBy(productStock.id);
//
//        // 2단계: 그 합계들 중 최소값을 찾는 외부 쿼리
//        NumberExpression<Integer> minTotalAdditionalPrice =
//            JPAExpressions
//                .select(additionalPriceSum.min())
//                .from(productStock)
//                .where(productStock.product.id.eq(product.id));
//
//        // 기본 가격 + 최소 추가 가격
//        NumberExpression<Integer> baseWithMinAddition = product.basePrice.add(minTotalAdditionalPrice);
//
//        // 할인율 적용
//        // (상품 기본 가격 + 추가 가격) * (100 - 할인율) / 100
//        NumberExpression<Integer> discountedPrice = Expressions.numberTemplate(Integer.class,
//            "({0} * (100 - {1})) / 100",
//            baseWithMinAddition, groupPurchase.discountRate);
//
//        return discountedPrice;
//    }

    private BooleanExpression endDateLoe(LocalDateTime endDateTo) {
        return endDateTo != null ? groupPurchase.endDate.loe(endDateTo) : null;
    }

    private BooleanExpression endDateGoe(LocalDateTime endDateFrom) {
        return endDateFrom != null ? groupPurchase.endDate.goe(endDateFrom) : null;
    }

    private BooleanExpression startDateLoe(LocalDateTime startDateTo) {
        return startDateTo != null ? groupPurchase.startDate.loe(startDateTo) : null;
    }

    private BooleanExpression startDateGoe(LocalDateTime startDateFrom) {
        return startDateFrom != null ? groupPurchase.startDate.goe(startDateFrom) : null;
    }

    private BooleanExpression titleContain(String title) {
        return hasText(title) ? groupPurchase.title.contains(title) : null;
    }

    private BooleanExpression statusEq(GroupPurchase.Status status) {
        return status != null ? groupPurchase.status.eq(status) : null;
    }


}

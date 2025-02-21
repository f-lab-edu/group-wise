package wj.flab.group_wise.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class ListUtils {

    private ListUtils() {
        throw new IllegalStateException("유틸 클래스는 인스턴스화할 수 없습니다.");
    }

    public interface ContainerOfValues<ValueType> {
        List<ValueType> getValues();
    }

    /**
     * 주어진 컨테이너들의 데카르트 곱을 계산합니다.
     * 빈 컨테이너나 null인 컨테이너는 무시됩니다.
     *
     * @param listOfContainer 값 컨테이너들의 리스트
     * @param <V> 값의 타입
     * @return 모든 가능한 조합을 포함하는 리스트
     * @throws NullPointerException listOfContainer가 null인 경우
     */
    public static <V> List<List<V>> cartesianProduct(List<? extends ContainerOfValues<V>> listOfContainer) {

        Objects.requireNonNull(listOfContainer, "listOfContainer must not be null");

        if (listOfContainer.isEmpty()) {
            return Collections.emptyList();
        }

        // null인 컨테이너는 제외
        List<ContainerOfValues<V>> validContainers = getValidContainers(listOfContainer);

        List<List<V>> resultList =  new ArrayList<>();
        buildCartesianProducts(validContainers, 0, new ArrayList<>(), resultList);
        return resultList;
    }

    private static <V> List<ContainerOfValues<V>> getValidContainers(List<? extends ContainerOfValues<V>> listOfContainer) {
        List<ContainerOfValues<V>> validContainers = new ArrayList<>();

        for (ContainerOfValues<V> container : listOfContainer) {
            if (container != null && container.getValues() != null && !container.getValues().isEmpty()) {
                validContainers.add(container);
            }
        }

        if (validContainers.isEmpty()) {
            return Collections.emptyList();
        }

        return validContainers;
    }

    private static <V> void buildCartesianProducts(
        List<? extends ContainerOfValues<V>> listOfContainer,
        int currentContainer,
        List<V> currentCombination,
        List<List<V>> resultList) {

        if (currentContainer == listOfContainer.size()) {
            resultList.add(new ArrayList<>(currentCombination));
            return;
        }

        ContainerOfValues<V> container = listOfContainer.get(currentContainer);
        for (V value : container.getValues()) {
            currentCombination.add(value);
            buildCartesianProducts(listOfContainer, currentContainer + 1, currentCombination, resultList);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

}

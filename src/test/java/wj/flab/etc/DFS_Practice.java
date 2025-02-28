package wj.flab.etc;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wj.flab.etc.DFS_Practice.Attribute.Value;

public class DFS_Practice {

    @RequiredArgsConstructor @Getter
    static class Attribute {
        public final String name;
        public final List<Value> vlist;

        @RequiredArgsConstructor @Getter
        static class Value implements Cloneable {
            public final String name;

            @Override
            protected Value clone() throws CloneNotSupportedException {
                return (Value) super.clone();
            }
        }
    }

    @RequiredArgsConstructor @Getter
    static class Stock {
        public final int s;
        public final List<Value> vlist;
    }

    public static void main(String[] args) {
        List<Attribute> attrList = new ArrayList<>();
        attrList.add(new Attribute(
                "a",
                List.of(
                    new Value("a1"),
                    new Value("a2")

                )
            ));
        attrList.add(new Attribute(
                "b",
                List.of(
                    new Value("b1"),
                    new Value("b2"),
                    new Value("b3")

                )
            )
        );
        attrList.add(new Attribute(
                "c",
                List.of(
                    new Value("c1"),
                    new Value("c2")
                )
            )
        );

        // while 문으로 DFS 짜보기
        /*
        작업기록:
        로직 작성이 어려워서 하루종일 고민했지만 끝내 잘 풀리지 않았음. ai 의 수정 도움 받음
        원래 작성한 백트레킹 로직이 명확하지 못하고 군데군데 분산되어있었음.

        <백트레킹 로직 필수 체크리스트>
         - 현재 상태 저장 방법
         - 다음 선택으로 진행하는 조건
         - 이전 상태로 돌아가는 조건
         - 해답을 찾았을 때의 처리
         - 종료 조건
         */

        int attrIdx = 0;
        int vIdx = 0;
        Map<Integer, Integer> AttributeValueIdxMap = new HashMap<>();
        List<Value> uniqueCombinationList = new ArrayList<>();
        List<Stock> result = new ArrayList<>();

        while (attrIdx >= 0) {
            Attribute attribute = attrList.get(attrIdx);
            List<Value> vlist = attribute.getVlist(); // 선택항목 값 리스트


            if (vIdx >= vlist.size()) {   // 현재 속성의 모든 값을 사용했다면
                if (attrIdx == 0) break;  // 첫 번째 속성까지 다 봤다면 종료

                // 백트레킹
                uniqueCombinationList.remove(uniqueCombinationList.size() - 1);
                AttributeValueIdxMap.put(attrIdx, 0);
                attrIdx--;
                vIdx = AttributeValueIdxMap.get(attrIdx) + 1;
                continue;
            }

            Value value = vlist.get(vIdx);
            uniqueCombinationList.add(value);
            AttributeValueIdxMap.put(attrIdx, vIdx); // 선택항목 값 인덱스 증가, 저장

            if (uniqueCombinationList.size() < attrList.size()) { // 조합이 완성되지 않았다면 다음 선택항목으로 이동
                attrIdx++;
                vIdx = 0;
            } else {
                // 조합 완성
                result.add(new Stock(1, new ArrayList<>(uniqueCombinationList)));
                uniqueCombinationList.remove(uniqueCombinationList.size() - 1);
                vIdx++;
            }
        }

        System.out.println("result = " + result);

    }

}

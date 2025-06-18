package project.global.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SkinIssueUtil {

    static final int ISSUE_COUNT = 13;

    /**
     * 인덱스 리스트를 받아서 SkinIssue 개수만큼 O/X 리스트 생성
     */
    public static List<String> generateOXListFromIndexes(List<Integer> selectedIndexes) {
        List<String> oxList = new ArrayList<>(Collections.nCopies(ISSUE_COUNT, "X"));

        if (selectedIndexes == null) {
            return oxList;
        }

        for (Integer index : selectedIndexes) {
            // 1-based 인덱스를 0-based로 변환, 범위 체크
            if (index != null && index >= 1 && index <= ISSUE_COUNT) {
                oxList.set(index - 1, "O");
            }
        }
        return oxList;
    }

    public static List<Integer> generateIndexesFromOXList(List<String> oxList) {
        List<Integer> selectedIndexes = new ArrayList<>();
        if (oxList == null) {
            return selectedIndexes;
        }
        for (int i = 0; i < oxList.size(); i++) {
            if ("O".equals(oxList.get(i))) {
                selectedIndexes.add(i + 1);  // 0-based 인덱스에 +1 해서 추가
            }
        }

        return selectedIndexes;
    }

}

package project.global.util;

import project.global.enums.skin.AxisType;
import project.global.enums.skin.SkinType;

public abstract class QuestionUtil {

    public static String findEnumByCode(Long code) {
        // 1. SkinType에서 찾기
        for (SkinType type : SkinType.values()) {
            if (type.getCode() == code) {
                return type.getString();
            }
        }

        // 2. AxisType에서 찾기
        for (AxisType type : AxisType.values()) {
            if (type.getCode() == code) {
                return type.toString();
            }
        }

        // 3. 못 찾으면 null 반환
        return null;
    }
}

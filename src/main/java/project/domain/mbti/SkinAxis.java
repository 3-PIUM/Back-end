package project.domain.mbti;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkinAxis {

    SKIN_TYPE("피부타입"),
    PIGMENT("색소"),     // 색소축
    MOISTURE("수분/유분"),    // 수분/유분 축
    REACTIVITY("반응성");   // 반응성 축

    private final String string;

    public static SkinAxis getString(String axis) {
        return Arrays.stream(SkinAxis.values())
            .filter(axis1 -> axis1.string.equals(axis))
            .findFirst()
            .orElse(null);

    }
}

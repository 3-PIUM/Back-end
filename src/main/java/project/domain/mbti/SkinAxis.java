package project.domain.mbti;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum SkinAxis {

    PIGMENT("색소"),     // 색소축
    MOISTURE("수분/유분"),    // 수분/유분 축
    REACTIVITY("반응성");   // 반응성 축

    private final String string;

    public static SkinAxis getSkinAxis(String axis) {
        return Arrays.stream(SkinAxis.values())
            .filter(axis1 -> axis1.string.equals(axis))
            .findFirst()
            .orElse(null);

    }
}

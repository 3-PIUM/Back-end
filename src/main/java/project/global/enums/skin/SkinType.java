package project.global.enums.skin;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkinType{
    A("건성", -1),
    O("지성", -3),
    N("복합성", -2);

    private final String string;
    private final int code;

    public static SkinType getSkinType(String skinType) {
        return Arrays.stream(SkinType.values())
            .filter(type -> type.string.equals(skinType))
            .findFirst()
            .orElse(null);
    }

}

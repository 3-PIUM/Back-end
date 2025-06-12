package project.global.enums.skin;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkinType {
    A("건성"),
    O("지성"),
    N("복합성");

    private final String string;

    public static SkinType getSkinType(String skinType) {
        return Arrays.stream(SkinType.values())
            .filter(type -> type.string.equals(skinType))
            .findFirst()
            .orElse(null);
    }

}

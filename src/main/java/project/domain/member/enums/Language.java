package project.domain.member.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {

    KR("한국어"),
    EN("English"),
    JP("日本語");

    private final String string;

    public static Language getLanguage(String name) {
        return Arrays.stream(Language.values())
            .filter(language -> language.getString().equals(name))
            .findFirst()
            .orElse(Language.KR);
    }

}

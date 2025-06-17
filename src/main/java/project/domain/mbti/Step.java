package project.domain.mbti;

public enum Step {
    START,
    MIDDLE,
    END;

    public static Boolean checkEnd(Step step) {
        return step != START && step != MIDDLE;
    }
}

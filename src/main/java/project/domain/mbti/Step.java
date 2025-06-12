package project.domain.mbti;

public enum Step {
    START,
    MIDDLE,
    E,
    B,
    F,
    S,
    R,
    W;

    public static Boolean checkEnd(Step step) {
        return step != START && step != MIDDLE;
    }
}

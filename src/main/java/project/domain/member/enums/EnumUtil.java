package project.domain.member.enums;

public abstract class EnumUtil {
    public static <T extends Enum<T>> T safeValueOf(Class<T> enumClass, String value) {
        if (value == null) return null;
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String toStringSafe(Enum<?> e) {
        return e != null ? e.toString() : null;
    }
}

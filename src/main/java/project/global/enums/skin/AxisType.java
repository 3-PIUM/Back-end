package project.global.enums.skin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AxisType {
    E(-5),
    B(-4),
    F(-6),
    S(-7),
    R(-8),
    W(-9);

    private final int code;

}

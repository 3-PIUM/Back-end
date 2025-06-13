package project.global.response.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "로그인 인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 로그인,회원가입, 인증
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "JWT4000",
            "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "JWT4001",
            "해당 refresh token이 존재하지 않습니다."),
    TOKEN_IS_EXPIRED(HttpStatus.BAD_REQUEST, "JWT4002",
            "만료된 토큰입니다."),
    AUTHENTICATION_TYPE_IS_NOT_BEARER(HttpStatus.BAD_REQUEST, "JWT4003",
            "잘못된 토큰 타입입니다."),

    /*
     * mbti 설문 검사
     */
    AXIS_NOT_FOUND(HttpStatus.NOT_FOUND, "MBTI_400",
            "해당 축에 관한 설문질문이 없습니다."),


    /*
     * mail
     */
    MAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "MAIL400", "잘못된 메일입니다."),
    MAIL_NOT_SEND(HttpStatus.BAD_REQUEST, "MAIL401", "메일 전송에 실패했습니다."),

    /*
     * member
     */
    MEMBER_NOT_FOUND_BY_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER4000",
            "해당 닉네임를 가진 회원이 존재하지 않습니다."),
    MEMBER_NOT_FOUND_BY_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER4001",
            "해당 email을 가진 회원이 존재하지 않습니다."),
    MEMBER_DUPLICATE_BY_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER4002",
            "이미 가입된 email입니다. "),
    MEMBER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER4003",
            "잘못된 비밀번호 입니다. "),
    MEMBER_DUPLICATE_BY_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER4004"
            , "이미 있는 닉네임입니다."),
    MEMBER_NOT_FOUND_BY_ID(HttpStatus.BAD_REQUEST, "MEMBER4005",
            "해당 id를 가진 회원이 존재하지 않습니다."),

    /*
     * cart
     */
    CART_NOT_FOUND(HttpStatus.BAD_REQUEST, "CART4000",
            "장바구니가 존재하지 않습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "CART4001",
            "장바구니에 해당 상품이 존재하지 않습니다."),

    /*
     * item
     */
    ITEM_NOT_FOUND(HttpStatus.BAD_REQUEST, "ITEM4000",
            "해당 상품이 존재하지 않습니다."),

    /**
     * itemimage
     */
    MAIN_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ITEM_IMAGE4000",
            "메인 이미지가 존재하지 않습니다."),
    DETAIL_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ITEM_IMAGE4001",
            "세부 이미지가 존재하지 않습니다."),


    /**
     * wishlist
     */
    WISHLIST_NOT_FOUND_BY_MEMBER(HttpStatus.BAD_REQUEST, "WISHLIST4000",
            "멤버ID에 해당하는 찜 목록이 존재하지 않습니다."),
    WISHLIST_NOT_FOUND(HttpStatus.BAD_REQUEST, "WISHLIST4001",
            "입력한 찜ID에 해당하는 테이블이 존재하지 않습니다."),
    WISHLIST_ITEM_EXIST(HttpStatus.BAD_REQUEST, "WISHLIST4002",
            "이미 찜 목록에 등록되어 있는 아이템입니다."),

    /**
     * reivew
     */
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "REVIEW4000",
            "입력된 리뷰ID에 해당하는 리뷰가 존재하지 않습니다."),
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    }

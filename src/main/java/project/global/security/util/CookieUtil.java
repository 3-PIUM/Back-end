package project.global.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.WebUtils;

public class CookieUtil {
    //도메인 나오면 변경될 값
    private static final String COOKIE_DOMAIN = "pium.com";

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .domain(COOKIE_DOMAIN)
                .maxAge(maxAge)
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie targetCookie = WebUtils.getCookie(request, name);
        if (targetCookie != null) {
            Cookie cookie = new Cookie(targetCookie.getName(), null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    //객체를 직렬화 (object to string)
    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize((Serializable) object));
    }

    //쿠키 역직렬화 (string to object)
    public static <T> T deserialize(Cookie cookie, Class<T> tClass) {
        return tClass.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}

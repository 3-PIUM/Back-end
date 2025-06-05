package project.global.security.filter;

import static project.global.response.status.ErrorStatus.AUTHENTICATION_TYPE_IS_NOT_BEARER;
import static project.global.response.status.ErrorStatus.TOKEN_IS_EXPIRED;

import project.domain.member.enums.Role;
import project.global.response.status.ErrorStatus;
import project.global.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter { //http 요청마다 Jwt인증 수행

    private static final String CHECK_URL = "/";
    private static final List<String> EXCLUDE_URL_PATTERN_LIST = List.of(
            "/swagger-ui",
            "/swagger-resources",
            "/v3/api-docs",
            "/auth",
            "/mail"
    );
    private static final String AUTHORIZATION_TYPE = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //String authorization = request.getHeader(AUTHORIZATION_HEADER); //Jwt 인증 정보 추출
        String authorization = jwtUtil.getJwtFromRequest(request);

        if (authorization == null) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorization.startsWith(AUTHORIZATION_TYPE)) {
            handleException(request, response, filterChain, AUTHENTICATION_TYPE_IS_NOT_BEARER);
            return;
        }

        String jwtToken = authorization.substring(AUTHORIZATION_TYPE.length());
        log.info("jwt: {}", jwtToken);

        if (jwtUtil.isExpired(jwtToken)) {
            handleException(request, response, filterChain, TOKEN_IS_EXPIRED);
            return;
        }

        Claims claims = jwtUtil.getPayload(jwtToken);
        String email = jwtUtil.getEmail(jwtToken);
        Role roleType = Role.valueOf(jwtUtil.getRoleType(jwtToken));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,
                null, List.of(roleType::toString));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info(request.toString());

        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain filterChain, ErrorStatus exception) throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        //예외 정보를 활용하기 위해 request에 설정
        request.setAttribute("authException", exception);

        // 클라이언트에 예외 정보 날림
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"error\": \"%s\", \"message\": \"%s\"}",
                exception.name(),
                exception.getMessage()));

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        //swagger 필터링 제외
        String path = request.getRequestURI();
        return EXCLUDE_URL_PATTERN_LIST.stream()
                .anyMatch(path::startsWith) || path.equals(CHECK_URL);
    }
}

package project.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import project.domain.member.Role;
import project.global.security.dto.UserDetailsDTO;
import project.global.security.service.redis.RefreshTokenService;
import project.global.security.util.CookieUtil;
import project.global.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Long ACCESS_TOKEN_VALIDITY_IN_SECONDS = 604800L;
    private static Long REFRESH_TOKEN_VALIDITY_IN_SECONDS = 1209600L;

    private static final String ACCESS_TOKEN_KEY = "access-token";
    private static final String REFRESH_TOKEN_KEY = "refresh-token";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);

            String email = credentials.get("email");
            String password = credentials.get("password");

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication)
            throws IOException, ServletException {
        UserDetailsDTO userDetails = (UserDetailsDTO) authentication.getPrincipal();

        //securityContext 저장
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        Long memberId = userDetails.getMemberId();
        String email = userDetails.getUsername();
        Role roleType = userDetails.getMember().getRole();

        //access token 생성 및 저장
        String accessToken = jwtUtil.createJwt(memberId, email, true, roleType);
        CookieUtil.addCookie(response, ACCESS_TOKEN_KEY, accessToken, ACCESS_TOKEN_VALIDITY_IN_SECONDS.intValue());

        //refresh token 생성 및 저장
        String refreshToken = jwtUtil.createJwt(memberId, email, false, roleType);
        refreshTokenService.saveRefreshToken(memberId, refreshToken);
        CookieUtil.addCookie(response, REFRESH_TOKEN_KEY, refreshToken, REFRESH_TOKEN_VALIDITY_IN_SECONDS.intValue());

        response.setStatus(HttpStatus.OK.value()); //성공하면 200 상태코드

        Map<String, String> tokens = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
        response.getWriter().write(objectMapper.writeValueAsString(tokens));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, String> erroResponse = Map.of(
                "error", "Authentication failed",
                "message", failed.getMessage()
        );
        response.getWriter().write(objectMapper.writeValueAsString(erroResponse));
    }
}

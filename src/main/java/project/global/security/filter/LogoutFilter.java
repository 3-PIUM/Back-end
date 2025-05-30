package project.global.security.filter;

import project.global.security.service.redis.RefreshTokenService;
import project.global.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class LogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        if (!requestURI.equals("/logout") || !request.getMethod().equalsIgnoreCase("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        //String authHeader = request.getHeader("Authorization");
        String authHeader = jwtUtil.getJwtFromRequest(request);
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("잘못된 Authorization 헤더 값 입니다.");
            return;
        }

        String token = authHeader.substring(7);

        if (jwtUtil.validateToken(token)) {
            Long memberId = jwtUtil.getMemberId(token);
            refreshTokenService.deleteRefreshToken(memberId);
            sendSuccessResponse(response, "Logout Successful");
        } else {
            sendErrorResponse(response,HttpStatus.UNAUTHORIZED, "Invalid Token");
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(message);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.getWriter().write(message);
    }

}

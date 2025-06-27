package project.global.config;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import project.global.security.filter.JwtAuthFilter;
import project.global.security.filter.LoginFilter;
import project.global.security.service.redis.RefreshTokenService;
import project.global.security.util.JwtUtil;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // cors 다 해결
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();
                                configuration.setAllowedOrigins(List.of(
                                        "http://localhost:5173", // 개발 환경
                                        "http://localhost:8080", // 개발 환경
                                        "http://Pium-LoadBalancer-1515701121.ap-northeast-2.elb.amazonaws.com", //로드 밸런서
                                        "https://pium-front-4iy3.vercel.app",
                                        "https://pium-front-git-main-hyemis-projects.vercel.app/"
                                ));

                                configuration.setAllowedMethods(
                                        List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // 허용할 HTTP 메서드
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);
                                configuration.setAllowCredentials(true);
                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;
                            }
                        }))
                //csrf disable
                .csrf(AbstractHttpConfigurer::disable)

                //form login 방식 disable
                .formLogin(AbstractHttpConfigurer::disable)

                //Http basic 인증 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable)

                //경로별 인가 작업
                .authorizeHttpRequests(authorize -> authorize
                        //TODO
                        // 관리자,사용자별로 접근 권한이 있는 사용자를 지정
                        // 인증이 되지 않으면 접근을 하지 못하도록
                        .anyRequest().permitAll()
                )

                //session 설정
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(new JwtAuthFilter(jwtUtil), LoginFilter.class)
                .addFilterAt(
                        new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenService),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new project.global.security.filter.LogoutFilter(jwtUtil, refreshTokenService),
                        LogoutFilter.class);
        return http.build();
    }

}

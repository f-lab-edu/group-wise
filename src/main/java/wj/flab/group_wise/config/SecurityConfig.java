package wj.flab.group_wise.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * csrf 토큰이 비활성화된 이유 : jwt 기반으로 인증을 하기 때문
     * jwt 토큰은 쿠키가 아닌 Authorization 헤더에 담겨서 전송되기 때문에 csrf 공격에 취약하지 않습니다.
     */
    @Bean @SuppressWarnings("java:S4502")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 인증 관련 API 허용
                .requestMatchers("/api/auth/**").permitAll()

                // API 문서 관련 경로 허용 ⭐
                .requestMatchers("/docs/**").permitAll()              // 커스텀 docs 경로
                .requestMatchers("/docs").permitAll()                 // 메인 docs 페이지
                .requestMatchers("/api/v3/api-docs/**").permitAll()   // API 스펙 JSON
                .requestMatchers("/swagger-ui/**").permitAll()        // UI 리소스들 (내부적으로 사용)
                .requestMatchers("/webjars/**").permitAll()           // WebJars 리소스

                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}

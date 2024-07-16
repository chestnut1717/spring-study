package com.example.springjwt.config;

import com.example.springjwt.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    // authenticationManager 의존성 주입을 위해 Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{

        return configuration.getAuthenticationManager();
    }

    // 패스워드 인코딩을 위한 Bcrypt 암호화 Bean 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. CSRF disable => session방식에서는 session이 고정되기 때문에 csrf 공격 방어 필수적
        // jwt에서는 session을 stateless상태로 관리 => csrf 공격 안해도 되어서 disable
        http
                .csrf((auth) -> auth.disable());

        // 2. From 로그인방식 disabled
        http
                .formLogin((auth) -> auth.disable());

        // 3. http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        // 4. AdminController, MainController의 각종 인가 작업 설정
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join").permitAll() // 해당 경로에서는 모든 권한 허락
                        .requestMatchers("/admin").hasRole("ADMIN") // /admin에서는 ADMIN에서만 접근 가능
                        .anyRequest().authenticated()); // 다른 요청에 대해서는 로그인한 사용자만 접근할 수 있게

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class);


        // JWT를 통한 인증/인가를 위해서 세션을 STATELESS 상태로 설정하는 것이 중요하다.
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}

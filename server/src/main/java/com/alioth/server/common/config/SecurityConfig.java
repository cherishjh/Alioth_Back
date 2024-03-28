package com.alioth.server.common.config;


import com.alioth.server.common.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(LoginApiUrl).permitAll()
                                .requestMatchers(MemberApiUrl).permitAll()
                                .requestMatchers(BoardApiUrl).permitAll()
                                .requestMatchers(ScheduleApiUrl).permitAll()
                                .requestMatchers(TeamApiUrl).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, BasicAuthenticationFilter.class);


        return httpSecurity.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> httpMethodList = List.of(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name()
        );
        List<String> ipList = List.of("http://localhost:9000");

        config.setAllowCredentials(true);
        config.setAllowedMethods(httpMethodList);
        config.setAllowedOrigins(ipList);
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    private static final String[] MemberApiUrl = {
            "/api/members/create",
            "/api/members/*/password",
            "/api/members/*/info",
            "/api/members/admin/update/*",
            "/api/members/admin/pr/*",
            "/api/members/details/*",
            "/api/members/details/update/*",
    };

    private static final String[] LoginApiUrl = {
            "/api/login",
            "/api/*/logout",
            "/api/test",
    };

    private static final String[] ScheduleApiUrl = {
            "/api/schedule/create",
            "/api/schedule/list",
            "/api/schedule/update",
            "/api/schedule/delete/*",
    };

    private static final String[] BoardApiUrl = {
            "/api/board/create",
            "/api/board/list",
            "/api/board/update",
            "/api/board/delete/*",
    };

    private static final String[] TeamApiUrl = {
            "/api/team/create",
            "/api/team/update/*",
            "/api/team/delete/*",

            "/api/team/detail/*",
            "/api/team/info/*",
            "/api/team/addMembers/*",
    };


}

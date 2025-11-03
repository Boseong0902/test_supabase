package com.example.supabase.config;

import com.example.supabase.service.SupabaseService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final SupabaseService supabaseService;

    public JwtFilter(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        String token = extractToken(request);

        if (token != null) {
            log.info("📝 토큰 발견 - 토큰 길이: {}", token.length());
            try {
                // SupabaseService를 통해 토큰 검증 및 사용자 정보 조회
                Map<String, Object> userInfo = supabaseService.getUserInfo(token);

                if (userInfo != null && userInfo.get("id") != null) {
                    // 인증 성공 - SecurityContext에 Authentication 설정
                    String userId = (String) userInfo.get("id");
                    Authentication authentication = createAuthentication(userId, userInfo);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.info("✅ JWT 인증 성공 - userId: {}", userId);
                } else {
                    log.warn("⚠️ 토큰은 유효하지만 사용자 정보가 없습니다.");
                }
            } catch (RuntimeException e) {
                log.error("❌ JWT 토큰 검증 실패: {}", e.getMessage());
            }
        } else {
            log.info("⚠️ 토큰 없음 - {} {}", method, requestURI);
        }

        filterChain.doFilter(request, response);
    }


    // Authorization 헤더에서 Bearer 토큰 추출
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()).trim();
        }

        return null;
    }

    //SecurityContext에 설정할 Authentication 객체 생성
    private Authentication createAuthentication(String userId, Map<String, Object> userInfo) {
        // 확장 방법 - public.users의 role 컬럼을 가져와서 설정
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                userId,
                null,
                authorities
        );

        authentication.setDetails(userInfo);
        
        log.info("🔐 Authentication 객체 생성 완료 - authenticated: {}, type: {}", 
                authentication.isAuthenticated(), authentication.getClass().getSimpleName());

        return authentication;
    }
}


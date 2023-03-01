package com.sparta.nyangdangback.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.nyangdangback.BaseResponseDto;
import com.sparta.nyangdangback.dto.SecurityExceptionDto;
import com.sparta.nyangdangback.user.dto.LoginRequestDto;
import com.sparta.nyangdangback.util.ErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthFilter  extends AbstractAuthenticationProcessingFilte {
////    private final ObjectMapper om;
////    public static final String USERNAME_EMPTY_MESSAGE = "아이디를 입력해 주세요";
////    public static final String PASSWORD_EMPTY_MESSAGE = "비밀번호를 입력해 주세요";
//    private final JwtUtil jwtUtil;
////    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login",
////            "POST");
////    private AuthenticationSuccessHandler authenticationSuccessHandler;
////    private AuthenticationFailureHandler authenticationFailureHandler;
////
////    public JwtAuthFilter(AuthenticationSuccessHandler authenticationSuccessHandler,
////                         AuthenticationFailureHandler authenticationFailureHandler,
////                         ObjectMapper om) {
////        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
////        this.om = om;
////        setAuthenticationSuccessHandler(authenticationSuccessHandler);
////        setAuthenticationFailureHandler(authenticationFailureHandler);
////    }
////    @Override
////    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
////        checkMethodAndContentType(request);
////        LoginRequestDto loginRequestDto = obtainLoinRequestDto(request);
////        String username = loginRequestDto.getUsername();
////        String password = loginRequestDto.getPassword();
////
////        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
////        setDetails(request, authRequest);
////        return get
////    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String token = jwtUtil.resolveToken(request);
//
//        if(token != null) {
//            if(!jwtUtil.validateToken(token)){
//                jwtExceptionHandler(response, "Token Error", HttpStatus.UNAUTHORIZED.value());
//                return;
//            }
//            Claims info = jwtUtil.getUserInfoFromToken(token);
//            setAuthentication(info.getSubject());
//        }
//        filterChain.doFilter(request,response);
//    }
//
//    private void setAuthentication(String username) {
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        Authentication authentication = jwtUtil.createAuthentication(username);
//        context.setAuthentication((authentication));
//
//        SecurityContextHolder.setContext(context);
//    }
//
//    public void jwtExceptionHandler(HttpServletResponse response, String msg, int statusCode) {
//        response.setStatus(statusCode);
//        response.setContentType("application/json");
//        try {
//            String json = new ObjectMapper().writeValueAsString(new SecurityExceptionDto(statusCode, msg));
//            response.getWriter().write(json);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }
//
//
//    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
//        this.authenticationSuccessHandler = authenticationSuccessHandler;
//    }
//
//    public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
//        return authenticationSuccessHandler;
//    }
//
//    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
//        this.authenticationFailureHandler = authenticationFailureHandler;
//    }
//
//    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
//        return authenticationFailureHandler;
//    }
//}

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 토큰을 가져온다.
        String token = jwtUtil.resolveToken(request);
        // 2. 토큰이 있는지 없는지 확인.
        if (token != null) {
            // 2-1. 토큰 검증에 오류가 있다면.
            if (!jwtUtil.validateToken(token)) {
                jwtExceptionHandler(response, ErrorCode.INVALID_TOKEN);
                return;
            }
            // 2-2. 토큰 검증에 오류가 없다면.
            Claims info = jwtUtil.getUserInfoFromToken(token);
            setAuthentication(info.getSubject());
        }
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 4. 토큰이 오류가 났을때, 우리가 Custom 한 값으로 Exception 처리 값을 알려준다.
    public void jwtExceptionHandler(HttpServletResponse response, ErrorCode errorCode) {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = new ObjectMapper().writeValueAsString(BaseResponseDto.of(errorCode.getHttpStatus(), errorCode.getDetail()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}



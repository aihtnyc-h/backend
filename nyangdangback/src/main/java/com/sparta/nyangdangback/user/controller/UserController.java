package com.sparta.nyangdangback.user.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.nyangdangback.jwt.JwtUtil;
import com.sparta.nyangdangback.user.dto.LoginRequestDto;
import com.sparta.nyangdangback.user.dto.SignupRequestDto;
import com.sparta.nyangdangback.user.service.KakaoService;
import com.sparta.nyangdangback.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    @GetMapping("/signup")
    public ModelAndView signupPage() {
        return new ModelAndView("signup");
    }

    // 로그인 한 유저가 메인페이지를 요청할 때 유저의 이름 반환
//    @GetMapping("/user-info")
//    @ResponseBody
//    public String getUserName(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return userDetails.getUsername();
//    }
//
//    @GetMapping("/login")
//    public ModelAndView loginPage() {
//        return new ModelAndView("login");
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {
        //userService.signup(signupRequestDto);
        return userService.signup(signupRequestDto, bindingResult);
        //return "회원가입 성공";//"redirect:/api/user/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse reponse) {
        userService.login(loginRequestDto, reponse);
        return "로그인 성공";
    }

    @GetMapping("/kakao/login")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String createToken = kakaoService.kakaoLogin(code, response);

        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/api/main";
    }
    // 아이디 중복확인
    //@GetMapping("/{username}/exists")
    @RequestMapping(value = "/{username}/exists", method = {RequestMethod.GET,RequestMethod.POST})
    //@PostMapping("/{username}/exists")
    public ResponseEntity<Boolean> checkUsernamerDuplicate(@PathVariable String username) {
        return ResponseEntity.ok(userService.checkUsernamerDuplicate(username));
    }
    //로그아웃 처리
//    public ModelAndView logout(HttpSession session) {
//        userService.logout(session);
//        ModelAndView mav = new ModelAndView();
//        mav.setViewName("username/login");
//        mav.addObject("msg", "logout");
//        return mav;
//    }
    @RequestMapping(value = "/logout", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    //@PostMapping("/logout")
    //@GetMapping("/logout")
    //@PutMapping("/logout")
    //@PatchMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        new SecurityContextHolder().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "로그아웃 성공"; // 내부 로직 성공, html파일 넣어주면 될것 해보기
        //"redirect:/api/user/login";
    }
}

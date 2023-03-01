package com.sparta.nyangdangback.user.service;

import com.sparta.nyangdangback.dto.MessageDto;
import com.sparta.nyangdangback.jwt.JwtUtil;
import com.sparta.nyangdangback.user.dto.LoginRequestDto;
import com.sparta.nyangdangback.user.dto.SignupRequestDto;
import com.sparta.nyangdangback.user.entity.User;
import com.sparta.nyangdangback.user.entity.UserRoleEnum;
import com.sparta.nyangdangback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;  //시큐리티
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";


//    @Transactional
//    public void signup(SignupRequestDto signupRequestDto){
//        String username = signupRequestDto.getUsername();
//        System.out.println(username);
//        if (username.length() < 4 || username.length() > 10){
//            throw new RuntimeException("아이디를 조건에 맞게 입력해주세요.");
//        }
//        if (!username.matches("^[0-9|a-z]*$")){
//            throw new RuntimeException("아이디를 조건에 맞게 입력해주세요.");
//        }
//        String password = signupRequestDto.getPassword();
//        System.out.println(password);
//        if (password.length() < 8 || password.length() > 15){
//            throw new RuntimeException("비밀번호를 조건에 맞게 입력해주세요.");
//        }
//        if (!password.matches("^[0-9|a-z|A-Z]*$")){
//            throw new RuntimeException("비밀번호를 조건에 맞게 입력해주세요.");
//        }
//
//        Optional<User> found = userRepository.findByUsername(username);
//        if (found.isPresent()){
//            throw  new IllegalArgumentException("중복된 사용자가 존재합니다.");
//        }
//
//        UserRoleEnum role = UserRoleEnum.USER;
//        if(signupRequestDto.isAdmin()){
//            if(signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)){
//                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
//            }
//            role = UserRoleEnum.ADMIN;
//        }
//
//        User user = new User(username,password,role);
//        userRepository.save(user);
//    }
//    @Transactional
//    public String signup(SignupRequestDto signupRequestDto, BindingResult bindingResult) {
//        String username = signupRequestDto.getUsername();
//        String password = passwordEncoder.encode(signupRequestDto.getPassword());
//
//        // 회원 중복 확인
//        if (bindingResult.hasErrors()) {
//            return String.valueOf(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
//        }
////        Optional<User> found = userRepository.findByUsername(username);
////        if (found.isPresent()) {
////            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
////        }
//        // 사용자 ROLE확인
//        UserRoleEnum role = UserRoleEnum.USER;
//        if (signupRequestDto.isAdmin()) {
//            if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
//                throw new IllegalArgumentException("관리자 암호가틀려 가입이 불가능 합니다.");
//            }
//            role = UserRoleEnum.ADMIN;
//        }
//        // 새로운 user 객체를 다시 만들어준 후
//        User user = User.builder()
//                .username(username)
//                .password(password)
//                .role(role)
//                .build();
//
//    // DB에 저장하기
// //   user =new
//
//    new User(username, password, role);
//        userRepository.save(user);
//        return "회원가입 성공";
//    //return ResponseEntity.ok(new MessageDto("회원가입 성공"));
//
//}

    @Transactional
    public ResponseEntity<?> signup(SignupRequestDto signupRequestDto, BindingResult bindingResult) {
        // username 규칙!
        String username = signupRequestDto.getUsername();
        if (username.length() < 4 || username.length() > 10) {
            throw  new IllegalArgumentException("아이디가 일치하지 않습니다.");
//            return new ResponseEntity(NOT_CONGITION_USERNAME.getHttpStatus());
        }
        if (!username.matches("^[0-9|a-z]*$")) {
            throw  new IllegalArgumentException("아이디가 일치하지 않습니다.");
//            return new ResponseEntity(NOT_CONGITION_USERNAME.getHttpStatus());
        }
        String password = signupRequestDto.getPassword();
        System.out.println(password);
        if (password.length() < 4 || password.length() > 11) {
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//            return new ResponseEntity(NOT_CONGITION_PASSWORD.getHttpStatus());
        }
        if (!password.matches("^[0-9|a-z|A-Z]*$")) {
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//            return new ResponseEntity(NOT_CONGITION_PASSWORD.getHttpStatus());
        }
//        if (bindingResult.hasErrors()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (bindingResult.hasErrors()){
            return ResponseEntity.ok(new MessageDto("회원가입 성공"));
//                    .badRequest()  // status : bad request
//                    .body(MessageDto.builder()  // body : SuccessResponseDto (statusCode, msg)
//                            .statusCode(HttpStatus.BAD_REQUEST.value())
//                            .msg(bindingResult.getAllErrors().get(0).getDefaultMessage())
//                            .build());
        }

        // 회원 중복 확인
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            new IllegalArgumentException("등록된 사용자가 없습니다.");
            // 얘외 처리하면 바로 넣기
//            return ResponseEntity.badRequest()  // status : bad request
//                    .body(MessageDto.builder()  // body : SuccessResponseDto (statusCode, msg)
//                            .statusCode(HttpStatus.BAD_REQUEST.value())
//                            .msg("중복된 사용자가 존재합니다.")
//                            .build());
        }

        //사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (signupRequestDto.isAdmin()) {
            if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                new IllegalArgumentException("등록된 사용자가 없습니다.");
            }
            role = UserRoleEnum.ADMIN;
        }
        // 새로운 user 객체를 다시 만들어준 후
        User user = User.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
        // DB에 저장하기
        userRepository.save(user);
        return ResponseEntity.ok(new MessageDto("회원가입 성공"));
    }
    // 회원 가입
//    @Transactional
//    public String signup(SignupRequestDto signupRequestDto) {
//        String username = signupRequestDto.getUsername();
//        System.out.println(username);
//        if (username.length() < 4 || username.length() > 11) {
//            throw new IllegalArgumentException("아이디를 조건에 맞게 입력해주세요.");
//        }
//        if (!username.matches("^[0-9|a-z]*$")) {
//            throw new IllegalArgumentException("아이디를 조건에 맞게 입력해주세요.");
//        }
//        String password = signupRequestDto.getPassword();
//        System.out.println(password);
//        if (password.length() < 4 || password.length() > 11) {
//            throw new IllegalArgumentException("비밀번호를 조건에 맞게 입력해주세요.");
//        }
//        if (!password.matches("^[0-9|a-z|A-Z]*$")){
//            throw new IllegalArgumentException("비밀번호를 조건에 맞게 입력해주세요.");
//        }
//        Optional<User> found = userRepository.findByUsername(username);
//        if (found.isPresent()){
//            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
//        }
//        UserRoleEnum role = UserRoleEnum.USER;
//        if (signupRequestDto.isAdmin()){
//            if (signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)){
//                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
//            }
//            role = UserRoleEnum.ADMIN;
//        }
//        User user =new User(username, password, role);
//        userRepository.save(user);
//        return username;
//    }

    @Transactional(readOnly = true)
    public void login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );
        // 비밀번호 확인
        if(!user.getPassword().equals(password)){
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(), user.getRole()));
    }

    @Transactional
    public boolean checkUsernamerDuplicate(String username) {
        return userRepository.existsByusername(username);
    }

//    public void logout() {
//    }

//    @Transactional
//    public void logout(HttpSession session) {
//        session.invalidate();
//        //return ; // 리턴 값 생각해보기!!!
//    }


}


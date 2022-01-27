package com.ssafy.deathnotelive.controller;

import com.ssafy.deathnotelive.config.jwt.JwtProperties;
import com.ssafy.deathnotelive.dto.LoginDto;
import com.ssafy.deathnotelive.dto.UserDto;
import com.ssafy.deathnotelive.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("user")
@Api(tags = {"User Controller"})
@Slf4j
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("login")
    @ApiOperation(value = "로그인", notes = "<strong>아이디와 패스워드</strong>를 통해 로그인 한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity login(
            @RequestBody @ApiParam(value = "로그인 정보", required = true) LoginDto.Request requestInfo, HttpServletResponse response) {

        String userId = requestInfo.getUserId();
        String password = requestInfo.getPassword();
        String token = userService.logIn(userId, password);
        Cookie cookie = new Cookie(JwtProperties.COOKIE_NAME, token);
        cookie.setMaxAge(JwtProperties.EXPIRATION_TIME); // 쿠키의 만료시간 설정
        cookie.setPath("/");
        response.addCookie(cookie);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("signup")
    @ApiOperation(value = "회원 가입", notes = "<strong>아이디와 패스워드</strong>를 통해 회원가입 한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity register(
            @RequestBody @ApiParam(value = "회원가입 정보", required = true) UserDto.UserRegist registerInfo) {

        userService.signup(registerInfo);
        return new ResponseEntity("회원가입에 성공했습니다.", HttpStatus.OK);
    }

    @PostMapping("{userId}")
    @ApiOperation(value = "유저 Detail 정보 조회", notes = "유저의 Detail 정보를 조회한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<UserDto.UserInfo> getUserInfo(
            @PathVariable @ApiParam(value = "정보를 조회할 유저 ID", required = true) String userId) {
        UserDto.UserInfo userInfo = userService.getUserInfo(userId);
        return new ResponseEntity(userInfo, HttpStatus.OK);
    }

    @PutMapping("{userId}")
    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보를 수정한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity modifyUser(
            @RequestBody @ApiParam(value = "정보를 수정할 유저 ID", required = true) UserDto.ModifyUserInfo modifyUserInfo) {
        userService.modifyUser(modifyUserInfo);
        return new ResponseEntity("회원 정보가 수정되었습니다.", HttpStatus.OK);
    }

    @DeleteMapping("{userId}")
    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴 한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity deleteUser(
            @PathVariable @ApiParam(value = "탈퇴할 유저 ID", required = true) String userId) {
        userService.deleteUser(userId);
        return new ResponseEntity("회원 탈퇴에 성공했습니다.", HttpStatus.OK);
    }
}
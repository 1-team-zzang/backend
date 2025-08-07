package com.example.calpick.domain.controller;
import com.example.calpick.domain.dto.auth.request.KakaoCodeRequest;
import com.example.calpick.domain.dto.auth.request.SignupRequest;
import com.example.calpick.domain.dto.auth.response.SignupResponse;
import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.dto.user.UserDto;
import com.example.calpick.domain.service.AuthService;
import com.example.calpick.domain.service.impl.KakaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth API")
public class AuthController {
    private final AuthService authService;
    private final KakaoService kakaoService;

    @PostMapping("/kakao/signup")
    public Mono<ResponseEntity<Response<UserDto>>> kakaoAuthorize(@Valid @RequestBody KakaoCodeRequest request){
        return kakaoService.kakaoAuthorize(request)
                .map(result -> {
                    ResponseCookie cookie = ResponseCookie.from("Refresh-Token", result.getRefreshToken())
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .sameSite("None")
                            .maxAge(60 * 60 * 24 * 7)
                            .build();

                    return ResponseEntity.ok()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + result.getAccessToken())
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .body(Response.success(result.getUserDto()));
                });
    }

    @PostMapping("/signup")
    public Response<SignupResponse> signup(@Valid @RequestBody SignupRequest request){
        return Response.success(authService.signUp(request));
    }

    @PostMapping("/test")
    public Response<Object> test(){
        return Response.success();
    }

    @PostMapping("/logout")
    public Response<Object> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response){
        authService.logout(userDetails.getEmail(), response);
        return Response.success();
    }

    @DeleteMapping("/withdraw")
    public Response<Object> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      HttpServletResponse response) {
        authService.withdraw(userDetails.getEmail(), response);
        return Response.success();
    }
}

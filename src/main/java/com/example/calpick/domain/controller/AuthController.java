package com.example.calpick.domain.controller;
import com.example.calpick.domain.dto.auth.request.SignupRequest;
import com.example.calpick.domain.dto.auth.response.SignupResponse;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request){
        return new ResponseEntity<>(authService.signUp(request), HttpStatus.CREATED);
    }

    @PostMapping("/test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("Success",HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response){
        authService.logout(userDetails.getEmail(), response);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      HttpServletResponse response) {
        authService.withdraw(userDetails.getEmail(), response);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}

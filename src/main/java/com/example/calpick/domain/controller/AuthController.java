package com.example.calpick.domain.controller;
import com.example.calpick.domain.dto.auth.request.SignupRequest;
import com.example.calpick.domain.dto.auth.response.SignupResponse;
import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public Response<SignupResponse> signup(@RequestBody SignupRequest request){
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

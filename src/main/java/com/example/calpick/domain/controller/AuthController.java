package com.example.calpick.domain.controller;
import com.example.calpick.domain.dto.auth.request.SignupRequest;
import com.example.calpick.domain.dto.auth.response.SignupResponse;
import com.example.calpick.domain.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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

}

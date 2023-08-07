package samat.learn.liquibase.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import samat.learn.liquibase.service.impl.LogooutService;
import samat.learn.liquibase.model.AuthenticationRequest;
import samat.learn.liquibase.model.AuthenticationResponse;
import samat.learn.liquibase.model.RegisterRequest;
import samat.learn.liquibase.service.impl.AuthenticationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final LogooutService logoutService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(service.register(request, httpServletRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(service.authenticate(request, httpServletRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request, @RequestBody String fingerprint)
            throws IOException {
        return ResponseEntity.ok(service.updateToken(request, fingerprint));
    }

    @GetMapping("/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        logoutService.logout(request, response, null);
    }
}
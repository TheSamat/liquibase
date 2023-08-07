package samat.learn.liquibase.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import samat.learn.liquibase.components.AccessToken;
import samat.learn.liquibase.components.CustomUserDetails;
import samat.learn.liquibase.components.JwtUtil;
import samat.learn.liquibase.entity.Session;
import samat.learn.liquibase.entity.User;
import samat.learn.liquibase.model.AuthenticationRequest;
import samat.learn.liquibase.model.AuthenticationResponse;
import samat.learn.liquibase.model.RegisterRequest;
import samat.learn.liquibase.repository.SessionRepository;
import samat.learn.liquibase.repository.UserRepository;
import samat.learn.liquibase.specification.UserSpecification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.rmi.RemoteException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    @Value("${project.domain}")
    private String domain;
    @Value("${application.security.jwt.expiration}")
    private int jwtExpiration;

    public AuthenticationResponse register(RegisterRequest request, HttpServletRequest httpServletRequest) {
        //TODO: this
        User user = new User(
                null,
                request.getFirstname(),
                request.getLastname(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        User savedUser = repository.save(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        AccessToken accessToken = jwtUtil.generateAccessToken(user, refreshToken);

        //TODO: this
        Session session = saveUserSession(user, refreshToken, request.getFingerprint());
        Long refreshTokenId = session.getId();

        HttpSession httpSession = httpServletRequest.getSession(true);
        httpSession.setAttribute("refreshTokenId", refreshTokenId);

        return new AuthenticationResponse(accessToken.getToken(), refreshTokenId, accessToken.getLifeTime());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        var otherSessions = sessionRepository.findAll();


        String refreshToken = jwtUtil.generateRefreshToken(user);
        AccessToken accessToken = jwtUtil.generateAccessToken(user, refreshToken);

        //TODO: this
        Session session = saveUserSession(user, refreshToken, request.getFingerprint());
        Long refreshTokenId = session.getId();

        HttpSession httpSession = httpServletRequest.getSession(true);
        httpSession.setAttribute("refreshTokenId", refreshTokenId);

        return new AuthenticationResponse(accessToken.getToken(), refreshTokenId, accessToken.getLifeTime());
    }

    public AuthenticationResponse updateToken(HttpServletRequest httpServletRequest, String fingerprint)
            throws IOException {

        final String authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        final String oldAccessToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer_")) {
            //TODO: this
            throw new RuntimeException();
        }

        oldAccessToken = authHeader.substring(7);
        userEmail = jwtUtil.extractUsername(oldAccessToken);

        //TODO: this
        if (userEmail == null)
            throw new RuntimeException();

        //TODO: this
        var user = this.repository.findByEmail(userEmail)
                .orElseThrow();

        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession == null)
            throw new RuntimeException();

        Long oldRefreshTokenId = (Long) httpSession.getAttribute("refreshTokenId");
        Session oldSession = sessionRepository.findById(oldRefreshTokenId)
                .orElseThrow();

        if (!oldSession.getFingerprint().equals(fingerprint))
            throw new RuntimeException("!fingerprintIsEqual");

        if (!jwtUtil.isTokenValid(oldSession.getRefreshToken(), new CustomUserDetails(user)))
            throw new RemoteException();

        oldSession.setRevoked(true);
        oldSession.setExpired(true);
        sessionRepository.save(oldSession);

        String refreshToken = jwtUtil.generateRefreshToken(user);
        AccessToken accessToken = jwtUtil.generateAccessToken(user, refreshToken);
        Session session = saveUserSession(user, refreshToken, fingerprint);
        Long refreshTokenId = session.getId();

        AuthenticationResponse authResponse = new AuthenticationResponse(accessToken.getToken(), refreshTokenId, accessToken.getLifeTime());

        return authResponse;
    }

    private Session saveUserSession(User user, String refreshToken, String fingerprint) {
        Session session = new Session(null, refreshToken, false, false, user, fingerprint);
        return sessionRepository.save(session);
    }
}
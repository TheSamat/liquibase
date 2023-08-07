package samat.learn.liquibase.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import samat.learn.liquibase.entity.Session;
import samat.learn.liquibase.repository.SessionRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class LogooutService implements LogoutHandler {
    private final SessionRepository sessionRepository;

    public LogooutService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer_")) {
            return;
        }

        jwt = authHeader.substring(7);

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null)
            return;

        Long oldRefreshTokenId = (Long) httpSession.getAttribute("refreshTokenId");
        httpSession.invalidate();

        Session session = sessionRepository.findById(oldRefreshTokenId)
                .orElseThrow();

        session.setExpired(true);
        sessionRepository.save(session);

        SecurityContextHolder.clearContext();
    }
}

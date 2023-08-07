package samat.learn.liquibase.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import samat.learn.liquibase.components.AccessToken;
import samat.learn.liquibase.components.CustomUserDetails;
import samat.learn.liquibase.entity.Session;
import samat.learn.liquibase.entity.User;

@Service
public class JwtUtil {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public AccessToken generateAccessToken(User user, String refreshToken) {
        UserDetails userDetails = new CustomUserDetails(user);
        String token = buildAccessToken(new HashMap<>(), userDetails, jwtExpiration, refreshToken);
        return new AccessToken(token, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        UserDetails userDetails = new CustomUserDetails(user);
        return buildRefreshToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildRefreshToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String buildAccessToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            String refreshToken
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        try {
            // Get Claims from valid token
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            // Get Claims from expired token
            return e.getClaims();
        }
    }

    private Key getSignInKey() {
        byte[] signature = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(signature);
    }
}
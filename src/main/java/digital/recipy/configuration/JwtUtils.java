package digital.recipy.configuration;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final String secretKey = "gweahrgse1faeh31gsdgs124152dehwer23523451edawfsa210edrq2124asfaes5135123faedgfe465241gsdfasdfgadgadfg32354adfgf";
    private final long jwtExpirationMs = 86400000;

    public String getUserNameFromJwtToken(String token) {
        return extractClaimFromToken(token, Claims::getSubject);
    }

    public Long getUserIdFromJwtToken(String token) {
        Claims claims = extractAllClaimsFromToken(token);
        return Long.parseLong(claims.get("userID").toString());
    }

    public String generateJwtToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());


        String roles = userDetails.getAuthorities().stream()
                .map(grantedAuthority -> ((SimpleGrantedAuthority) grantedAuthority).getAuthority())
                .collect(Collectors.joining(","));


        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .claim("userID", userDetails.getUserId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }


    public boolean validateJwtToken(String authToken) {
        try {
            logger.debug("Validating JWT token: {}", authToken);
            Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(authToken);
            logger.debug("JWT token is valid.");
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        logger.debug("JWT token is invalid.");
        return false;
    }



    private <T> T extractClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}

package com.example.app.security;

import com.example.app.exception.JwtParseException;
import com.example.app.exception.JwtParserException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class JwtTokenUtil {

    @Value("${jwt.claims.roles}")
    private String rolesFieldName;

    @Value("${jwt.public.key}")
    private String jwtPublicKey;

    private JwtParser jwtParser;

    public boolean validateToken(String token) {
        String username = getUsernameFromToken(token);
        return !isNull(username) && !isTokenExpired(token);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoleList(String token) {
        return getClaimFromToken(token, claims -> claims.get(rolesFieldName, List.class));
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return getJwtParser().parseSignedClaims(token).getPayload();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Error while creating JWT Parser", e);
            throw new JwtParserException("Error while creating JWT Parser for key: '%.10s'".formatted(jwtPublicKey));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error while parsing token", e);
            throw new JwtParseException("Error while parsing token: '%s'".formatted(token));
        } catch (Exception e) {
            log.error("Unknown error while parsing token", e);
            throw new JwtParseException("Unknown error while parsing token: '%s'".formatted(token));
        }
    }

    private JwtParser getJwtParser() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (this.jwtParser == null) {
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(jwtPublicKey));
            final PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
            this.jwtParser = Jwts.parser().verifyWith(publicKey).build();
        }
        return jwtParser;
    }

}

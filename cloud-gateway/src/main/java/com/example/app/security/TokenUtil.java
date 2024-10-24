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
public class TokenUtil {

    @Value("${jwt.claims.roles}")
    private String rolesFieldName;

    @Value("${jwt.public.key}")
    private String jwtPublicKey;

    private JwtParser jwtParser;

    public Claims getClaims(String token) {
        try {
            return getJwtParser().parseSignedClaims(token).getPayload();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Error while creating JWT Parser", e);
            throw new JwtParserException("Error while creating JWT Parser for key: '%.10s'".formatted(jwtPublicKey), e);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error while parsing token", e);
            throw new JwtParseException("Error while parsing token: '%s'".formatted(token), e);
        } catch (Exception e) {
            log.error("Unknown error while parsing token", e);
            throw new JwtParseException("Unknown error while parsing token: '%s'".formatted(token), e);
        }
    }

    public boolean validate(Claims claims) {
        final String username = claims.getSubject();
        return !isNull(username) && !isTokenExpired(claims);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        return getClaimFromToken(claims, c -> c.get(rolesFieldName, List.class));
    }

    private boolean isTokenExpired(Claims claims) {
        Date expirationDate = getExpirationDateFromToken(claims);
        return expirationDate.before(new Date());
    }

    private Date getExpirationDateFromToken(Claims claims) {
        return getClaimFromToken(claims, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(Claims claims, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(claims);
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

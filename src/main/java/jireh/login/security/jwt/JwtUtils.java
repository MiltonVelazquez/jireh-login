package jireh.login.security.jwt;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;
    
    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    //Generar un token
    public String generateAccesToken(String email){
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
            .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    //validar token de acceso
    public boolean isTokenValid(String token){
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return true;
        } catch (Exception e) {
            System.out.print("Token invalido, error: " + e.getMessage());
            return false;
        }
    }

    // obtener el email del token
    public String getEmailFromToken(String token){
        return getClaim(token, Claims::getSubject);
    }

    //podria dar error el Function, no se cual se importa
    //Obtener un solo claim
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // Obtener todos los claims del token
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
            .setSigningKey(getSignatureKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // obtener firma de token
    public Key getSignatureKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}


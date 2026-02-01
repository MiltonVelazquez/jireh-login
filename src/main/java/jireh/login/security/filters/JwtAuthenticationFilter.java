package jireh.login.security.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jireh.login.models.UserEntity;
import jireh.login.security.jwt.JwtUtils;
import jireh.login.repositories.UserRepository;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private JwtUtils jwtUtils;
    private UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserRepository userRepository){
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        
        UserEntity userEntity = null;
        String email;
        String password;

        try {
            userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
            email = userEntity.getEmail();
            password = userEntity.getPassword();
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch(DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        

        User user = (User) authResult.getPrincipal();
        String token = jwtUtils.generateAccesToken(user.getUsername());

        UserEntity userEntity = userRepository.findByEmail(user.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado tras autenticación"));
        
        List<String> roles = user.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .toList();

        response.addHeader("Authorization", token);
        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("id", userEntity.getId());
        httpResponse.put("name", userEntity.getName());
        httpResponse.put("lastname", userEntity.getLastname());
        httpResponse.put("number", userEntity.getNumber());
        httpResponse.put("email", userEntity.getEmail());
        httpResponse.put("roles", roles);
        httpResponse.put("message", "Autenticación correcta");

        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().flush();
    }
    
}

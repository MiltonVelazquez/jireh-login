package jireh.login.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jireh.login.models.UserEntity;
import jireh.login.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String createPasswordResetToken(String email) {
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        return token;
    }

    public boolean validateToken(String token) {
        return userRepository.findByResetToken(token)
            .map(user -> user.getResetTokenExpiry().isAfter(LocalDateTime.now()))
            .orElse(false);
    }
}
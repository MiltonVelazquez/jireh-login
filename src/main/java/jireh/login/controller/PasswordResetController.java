package jireh.login.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jireh.login.models.UserEntity;
import jireh.login.repositories.UserRepository;
import jireh.login.service.EmailService;
import jireh.login.service.UserService;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private UserService resetService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

    boolean userExists = userRepository.findByEmail(email).isPresent();

    if (!userExists) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "El correo electrónico no está registrado en nuestro sistema."));
    }

    try {
        String token = resetService.createPasswordResetToken(email);
        emailService.sendResetPasswordEmail(email, token);
        
        return ResponseEntity.ok(Map.of("message", "Se ha enviado un enlace de recuperación a tu bandeja de entrada."));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Ocurrió un error al procesar la solicitud de recuperación."));
    }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");

        if (!resetService.validateToken(token)) {
            return ResponseEntity.badRequest().body("Token inválido o expirado");
        }

        UserEntity user = userRepository.findByResetToken(token).get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada con éxito");
    }
}
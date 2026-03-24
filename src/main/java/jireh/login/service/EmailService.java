package jireh.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendResetPasswordEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail); 
            helper.setTo(to);
            helper.setSubject("Recuperación de Contraseña - Jireh");
            
            String url = "https://eddc0f65-a17f-4af9-9774-b722606f3449-00-2zbw4m3gak2ba.worf.replit.dev/reset-password?token=" + token;
            
            String htmlContent = "<h3>Solicitud de cambio de contraseña</h3>"
                    + "<p>Haz clic en el enlace para restablecer tu clave:</p>"
                    + "<a href='" + url + "'>Restablecer Contraseña</a>"
                    + "<p>El enlace caduca en 15 minutos.</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); 
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }
}

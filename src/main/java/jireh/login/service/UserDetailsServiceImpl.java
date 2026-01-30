package jireh.login.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jireh.login.models.UserEntity;
import jireh.login.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        UserEntity userEntity = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("El email " + email + " no se encuentra registrado."));

        Collection<? extends GrantedAuthority> authorities = userEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) // ACA CAPAZ VA UN .name() NO ENTIENDO POR QUE NO FUNCIONA AHORA
        .collect(Collectors.toSet());

        return new User(userEntity.getEmail(), userEntity.getPassword(), true, true, true, true, authorities);
    }

}

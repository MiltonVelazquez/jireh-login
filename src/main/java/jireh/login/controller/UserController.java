package jireh.login.controller;

import jireh.login.controller.request.RegisterDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jireh.login.models.UserEntity;
import jireh.login.repositories.RoleRepository;
import jireh.login.repositories.UserRepository;
import jireh.login.models.RoleEntity;

@RestController
@RequestMapping(path = "users/user")
public class UserController{

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO){

    Set<RoleEntity> roles = registerDTO.getRoles().stream()
      .map(roleName -> roleRepository.findByName(roleName)
           .orElseGet(() -> roleRepository.save(new RoleEntity(null, roleName))))
      .collect(Collectors.toSet());

    UserEntity userEntity = UserEntity.builder()
      .email(registerDTO.getEmail())
      .password(passwordEncoder.encode(registerDTO.getPassword()))
      .name(registerDTO.getName())
      .lastname(registerDTO.getLastname())
      .number(registerDTO.getNumber())
      .roles(roles)
      .build();

    userRepository.save(userEntity);

    return ResponseEntity.ok(userEntity);
  }

  @DeleteMapping("/deleteUser")
  public String deleteUser(@RequestParam String id){
    userRepository.deleteById(Long.parseLong(id));
    return "Se ha borrado el user con id".concat(id);
  }

  @GetMapping
  public Iterable<UserEntity> getAll(){
    return userRepository.findAll();
  }

  @GetMapping("/{id}")
  public Optional<UserEntity> getById(@PathVariable("id") Long id){
    return userRepository.findById(id);
  }

  @GetMapping("/search/{input}")
  public ResponseEntity<List<UserEntity>> search(@PathVariable("input") String input){
      List<UserEntity> results = userRepository.findByNameContainingIgnoreCase(input);
      return ResponseEntity.ok(results);
  }

}
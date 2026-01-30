package jireh.login.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


import jireh.login.models.RoleEntity;
import jireh.login.repositories.RoleRepository;

@RestController
@RequestMapping(path = "users/role")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public Iterable<RoleEntity> getAll(){
        return roleRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<RoleEntity> getById(@PathVariable("id") Long id){
        return roleRepository.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        roleRepository.deleteById(id);
    }

    @PostMapping
    public void saveUpdate(@RequestBody RoleEntity roleEntity){
        roleRepository.save(roleEntity);
    }

    @GetMapping("/search/{input}")
    public ResponseEntity<List<RoleEntity>> search(@PathVariable("input") String input){
        List<RoleEntity> results = roleRepository.findByNameContainingIgnoreCase(input);
        return ResponseEntity.ok(results);
    }

}

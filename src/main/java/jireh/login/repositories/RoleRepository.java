package jireh.login.repositories;

import jireh.login.models.RoleEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long>{

    Optional<RoleEntity> findByName(String name);

    List<RoleEntity> findByNameContainingIgnoreCase(String name);

}
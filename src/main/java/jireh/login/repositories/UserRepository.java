package jireh.login.repositories;

import jireh.login.models.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>{

  List<UserEntity> findByNameContainingIgnoreCase(String name);

  Optional<UserEntity> findByEmail(String email);

  @Query("select u from UserEntity u where u.email = ?1")
  Optional<UserEntity> getEmail(String email);

  Optional<UserEntity> findByResetToken(String token);

}
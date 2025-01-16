package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.UserModel;

// public interface UserRepo extends CrudRepository<UserModel, Long> {
// }

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface UserRepo extends JpaRepository<UserModel, Integer> {
  UserModel findByUsername(String username);
}

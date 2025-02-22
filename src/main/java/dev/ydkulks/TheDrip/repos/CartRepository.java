package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.CartModel;
import dev.ydkulks.TheDrip.models.UserModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface CartRepository extends JpaRepository<CartModel, Integer> {
  CartModel findByUser(UserModel user);
}

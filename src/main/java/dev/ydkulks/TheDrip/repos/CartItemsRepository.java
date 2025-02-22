package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.CartItemsModel;
import dev.ydkulks.TheDrip.models.CartModel;
import dev.ydkulks.TheDrip.models.ProductModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface CartItemsRepository extends JpaRepository<CartItemsModel, Integer> {
  CartItemsModel findByCartAndProduct(CartModel cart, ProductModel product);
}

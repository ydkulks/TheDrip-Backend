package dev.ydkulks.TheDrip.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.CartItemsModel;
import dev.ydkulks.TheDrip.models.CartModel;
import dev.ydkulks.TheDrip.models.ProductModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface CartItemsRepository extends JpaRepository<CartItemsModel, Integer>, JpaSpecificationExecutor<CartItemsModel> {
  CartItemsModel findByCartAndProduct(CartModel cart, ProductModel product);
  Page<CartItemsModel> findByCart(CartModel cart, Pageable pageable);
  // Page<CartItemsModel> findByCartAndProductAndSizeAndColor(
  //     CartModel cart,
  //     ProductModel product,
  //     String size,
  //     String color,
  //     Pageable pageable
  //   );
  // Page<CartItemsModel> findByCartAndProduct(
  //     CartModel cart,
  //     ProductModel product,
  //     Pageable pageable
  //   );
  CartItemsModel findByCartAndProductAndSizeAndColor(
      CartModel cart,
      ProductModel product,
      String size,
      String color
    );
}

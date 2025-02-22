package dev.ydkulks.TheDrip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.CartItemsModel;
import dev.ydkulks.TheDrip.models.CartModel;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.repos.CartItemsRepository;
import dev.ydkulks.TheDrip.repos.CartRepository;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.UserRepo;
import jakarta.transaction.Transactional;

@Service
public class CartService {
  @Autowired private CartRepository cartRepository;
  @Autowired private CartItemsRepository cartItemsRepository;
  @Autowired private UserRepo userRepo;
  @Autowired private ProductRepository productRepository;

  @Transactional
  public void addToOrUpdateCart(Integer userId, Integer productId, Integer quantity) {
    UserModel user = userRepo.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));
    ProductModel product = productRepository
      .findById(productId).orElseThrow(() -> 
          new IllegalArgumentException("Product not found"));

    // Find existing cart for the user
    CartModel cart = cartRepository.findByUser(user);
    if (cart == null) {
      cart = new CartModel();
      cart.setUser(user);
      cart = cartRepository.save(cart);
    }

    // Find existing cart item for the product in the cart
    CartItemsModel cartItem = cartItemsRepository
      .findByCartAndProduct(cart, product);
    if (cartItem == null) {
      // If the item doesn't exist, create a new one
      cartItem = new CartItemsModel();
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(quantity);
      cartItemsRepository.save(cartItem);
    } else {
      // If the item exists, update the quantity
      cartItem.setQuantity(cartItem.getQuantity() + quantity);
    }
  }
}

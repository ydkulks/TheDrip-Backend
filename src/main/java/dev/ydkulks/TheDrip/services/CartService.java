package dev.ydkulks.TheDrip.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.CartItemsModel;
import dev.ydkulks.TheDrip.models.CartItemsSpecification;
import dev.ydkulks.TheDrip.models.CartModel;
import dev.ydkulks.TheDrip.models.CartResponseWithTotalDTO;
import dev.ydkulks.TheDrip.models.ProductColorsModel;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.ProductSizesModel;
import dev.ydkulks.TheDrip.models.ProductSpecification;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.repos.CartItemsRepository;
import dev.ydkulks.TheDrip.repos.CartRepository;
import dev.ydkulks.TheDrip.repos.ProductColorsRepository;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.ProductSizesRepository;
import dev.ydkulks.TheDrip.repos.UserRepo;
import jakarta.transaction.Transactional;

@Service
public class CartService {
  @Autowired private CartRepository cartRepository;
  @Autowired private CartItemsRepository cartItemsRepository;
  @Autowired private UserRepo userRepo;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductSizesRepository productSizesRepository;
  @Autowired private ProductColorsRepository productColorsRepository;

  @Transactional
  public void createNewCartItem( Integer userId, Integer productId, Integer quantity, String color, String size) {
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
      .findByCartAndProductAndSizeAndColor(cart, product, size, color);
    if (cartItem == null) {
      // If the item doesn't exist, create a new one
      cartItem = new CartItemsModel();
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(quantity);
      cartItem.setColor(color);
      cartItem.setSize(size);
    }
    cartItemsRepository.save(cartItem);
  }

  @Transactional
  public void updateCartItemQuantity( Integer userId, Integer productId, Integer newQuantity, String color, String size) {
    UserModel user = userRepo.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));
    ProductModel product = productRepository
      .findById(productId).orElseThrow(() ->
          new IllegalArgumentException("Product not found"));

    // Find existing cart for the user
    CartModel cart = cartRepository.findByUser(user);
    System.out.println("Cart exists? " + cart);
    if (cart == null) {
      cart = new CartModel();
      cart.setUser(user);
      cart = cartRepository.save(cart);
    }

    // Find existing cart item for the product in the cart
    CartItemsModel cartItem = cartItemsRepository
      .findByCartAndProductAndSizeAndColor(cart, product, size, color);
    if (cartItem != null) {
      cartItem.setQuantity(newQuantity);
    }
    cartItemsRepository.save(cartItem);
  }

  @Transactional
  public CartResponseWithTotalDTO getItems(
      Integer userId,
      String productName,
      Integer sizeId,
      Integer colorId,
      String sortBy,
      String sortDirection,
      Pageable pageable
      ){

    Specification<ProductModel> spec =
        Specification.where(ProductSpecification.hasSearchTerm(productName));

    UserModel user = userRepo.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));
    // Handle null value to moke it optional
    ProductModel product = productName != null
      ? productRepository.findOne(spec)
      .orElseThrow(() -> new IllegalArgumentException("Product not found"))
      : null;
    ProductSizesModel size = sizeId != null
      ? productSizesRepository.findById(sizeId)
      .orElseThrow(() -> new IllegalArgumentException("Size not found"))
      : null;
    ProductColorsModel color = colorId != null
      ? productColorsRepository.findById(colorId)
      .orElseThrow(() -> new IllegalArgumentException("Color not found"))
      : null;
    CartModel cart = cartRepository.findByUser(user);

    if (cart == null) {
      // return new PageImpl<>(Collections.emptyList(), pageable, 0);
      // return new CartResponseWithTotalDTO(Collections.emptyList(), 0.0);
      return null;
    }

    Sort sort = null;
    if (sortBy != null && !sortBy.isEmpty()) {
      Sort.Direction direction =
        sortDirection != null && sortDirection.equalsIgnoreCase("desc")
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
      sort = Sort.by(direction, sortBy);
    }

    // Create a new Pageable object with the Sort information
    Pageable sortedPageable = pageable;
    if (sort != null) {
      sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    // Build the specification
    Specification<CartItemsModel> cartspec = Specification
      .where((root, query, criteriaBuilder) ->
          criteriaBuilder.equal(root.get("cart"), cart));

    // Add optional criteria
    if (product != null) {
      cartspec = cartspec.and(CartItemsSpecification.hasProduct(product));
    }
    if (size != null && size != null) {
      cartspec = cartspec.and(CartItemsSpecification.hasSize(size.getSize_name()));
    }
    if (color != null && color != null) {
      cartspec = cartspec.and(CartItemsSpecification.hasColor(color.getColor_name()));
    }

    Page<CartItemsModel> cartItemsPage = cartItemsRepository.findAll(cartspec, sortedPageable);

    List<CartItemsModel> allItems = cartItemsRepository.findAll(cartspec);
    double total = allItems.stream()
            .mapToDouble(item -> item.getProduct().getProductPrice() * item.getQuantity())
            .sum();

    return new CartResponseWithTotalDTO(cartItemsPage, total);
  }

  @Transactional
  public void removeFromCart(Integer cartItemId) {
    Optional<CartItemsModel> item = cartItemsRepository.findById(cartItemId);
    if (item != null ) {
      cartItemsRepository.deleteById(cartItemId);
    } else {
      throw new RuntimeException("Failed to remove cart item number " + cartItemId);
    }
  }
}

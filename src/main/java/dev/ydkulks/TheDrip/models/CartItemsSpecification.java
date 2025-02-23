package dev.ydkulks.TheDrip.models;

import org.springframework.data.jpa.domain.Specification;

public class CartItemsSpecification {

  public static Specification<CartItemsModel> hasProduct(ProductModel product) {
    return (root, query, criteriaBuilder) -> {
      if (product == null) {
        return criteriaBuilder.conjunction(); // Always true
      }
      return criteriaBuilder.equal(root.get("product"), product);
    };
  }

  public static Specification<CartItemsModel> hasSize(String size) {
    return (root, query, criteriaBuilder) -> {
      if (size == null || size.isEmpty()) {
        return criteriaBuilder.conjunction(); // Always true
      }
      return criteriaBuilder.equal(root.get("size"), size);
    };
  }

  public static Specification<CartItemsModel> hasColor(String color) {
    return (root, query, criteriaBuilder) -> {
      if (color == null || color.isEmpty()) {
        return criteriaBuilder.conjunction(); // Always true
      }
      return criteriaBuilder.equal(root.get("color"), color);
    };
  }
}

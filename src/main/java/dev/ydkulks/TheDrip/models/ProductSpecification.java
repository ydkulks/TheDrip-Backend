package dev.ydkulks.TheDrip.models;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
  public static Specification<ProductModel> hasCategory(
      ProductCategoriesModel category) {
    return (root, query, criteriaBuilder) ->
        category == null
            ? criteriaBuilder.conjunction()
            : criteriaBuilder.equal(root.get("category"), category);
  }

  public static Specification<ProductModel> hasUser(UserModel user) {
    return (root, query, criteriaBuilder) ->
        user == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("user"), user);
  }

  public static Specification<ProductModel> hasSeries(ProductSeriesModel series) {
    return (root, query, criteriaBuilder) ->
        series == null
            ? criteriaBuilder.conjunction()
            : criteriaBuilder.equal(root.get("series"), series);
  }

  public static Specification<ProductModel> hasPriceBetween(
      Double minPrice, Double maxPrice) {
    return (root, query, criteriaBuilder) -> {
      if (minPrice == null && maxPrice == null) {
        return criteriaBuilder.conjunction();
      } else if (minPrice == null) {
        return criteriaBuilder.lessThanOrEqualTo(root.get("productPrice"), maxPrice);
      } else if (maxPrice == null) {
        return criteriaBuilder.greaterThanOrEqualTo(root.get("productPrice"), minPrice);
      } else {
        return criteriaBuilder.between(root.get("productPrice"), minPrice, maxPrice);
      }
    };
  }
}

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

  public static Specification<ProductModel> hasSearchTerm(String searchTerm) {
    return (root, query, criteriaBuilder) -> {
      if (searchTerm == null || searchTerm.isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      String searchTermLower = searchTerm.toLowerCase();

      return criteriaBuilder.or(
          criteriaBuilder.like(
              criteriaBuilder.lower(root.get("productName")), "%" + searchTermLower + "%"),
          criteriaBuilder.like(
              criteriaBuilder.lower(root.get("productDescription")), "%" + searchTermLower + "%"),
          criteriaBuilder.like(
              criteriaBuilder.lower(root.get("category").get("categoryName")),
              "%" + searchTermLower + "%"),
          criteriaBuilder.like(
              criteriaBuilder.lower(root.get("series").get("series_name")),
              "%" + searchTermLower + "%"),
          criteriaBuilder.like(
              criteriaBuilder.lower(root.get("user").get("username")),
              "%" + searchTermLower + "%"));
    };
  }
}

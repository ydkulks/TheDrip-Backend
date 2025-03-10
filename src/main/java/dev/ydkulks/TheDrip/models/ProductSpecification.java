package dev.ydkulks.TheDrip.models;

import java.util.List;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

  public static Specification<ProductModel> hasColorsIn(List<ProductColorsModel> colors) {
    return new Specification<ProductModel>() {
      @Override
      public Predicate toPredicate(Root<ProductModel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (colors == null || colors.isEmpty()) {
          return criteriaBuilder.conjunction(); // Don't filter if no colors are specified
        }

        // Join the ProductModel with the ProductColorsModel through the 'colors' collection
        Join<ProductModel, ProductColorsModel> colorJoin = root.join("colors");

        // Create an OR predicate: color in (colors)
        return colorJoin.in(colors);
      }
    };
  }

  public static Specification<ProductModel> hasSizesIn(List<ProductSizesModel> sizes) {
    return new Specification<ProductModel>() {
      @Override
      public Predicate toPredicate(Root<ProductModel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (sizes == null || sizes.isEmpty()) {
          return criteriaBuilder.conjunction(); // Don't filter if no sizes are specified
        }

        // Join the ProductModel with the ProductSizesModel through the 'sizes' collection
        Join<ProductModel, ProductSizesModel> sizeJoin = root.join("sizes");

        // Create an OR predicate: size in (sizes)
        return sizeJoin.in(sizes);
      }
    };
  }

  public static Specification<ProductModel> hasProductStock(Boolean inStock) {
    return new Specification<ProductModel>() {
      @Override
      public Predicate toPredicate(
          Root<ProductModel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (inStock == null) {
          return criteriaBuilder.conjunction(); // Don't filter if inStock is not specified
        } else if (inStock) {
          // Filter for products with stock greater than 0
          return criteriaBuilder.greaterThan(root.get("productStock"), 0);
        } else {
          // Filter for products with stock equal to 0
          return criteriaBuilder.equal(root.get("productStock"), 0);
        }
          }
    };
  }

  public static Specification<ProductModel> hasCategoryIn( List<ProductCategoriesModel> categories) {
    return (root, query, criteriaBuilder) -> {
      if (categories == null || categories.isEmpty()) {
        return criteriaBuilder.conjunction(); // No category specified, don't filter
      } else {
        // Build an OR predicate: category in (categories)
        Predicate categoryPredicate = root.get("category").in(categories);
        return categoryPredicate;
      }
    };
  }

  public static Specification<ProductModel> hasUser(UserModel user) {
    return (root, query, criteriaBuilder) ->
        user == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("user"), user);
  }

  public static Specification<ProductModel> hasSeriesIn(
      List<ProductSeriesModel> series) {
    return (root, query, criteriaBuilder) -> {
      if (series == null || series.isEmpty()) {
        return criteriaBuilder.conjunction(); // No series specified, don't filter
      } else {
        // Build an OR predicate: series in (series)
        Predicate seriesPredicate = root.get("series").in(series);
        return seriesPredicate;
      }
    };
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
              criteriaBuilder.lower(root.get("series").get("seriesName")),
              "%" + searchTermLower + "%"),
          criteriaBuilder.like(
              criteriaBuilder.lower(root.get("user").get("username")),
              "%" + searchTermLower + "%"));
    };
  }
}

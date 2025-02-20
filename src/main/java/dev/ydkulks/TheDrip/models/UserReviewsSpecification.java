package dev.ydkulks.TheDrip.models;

import org.springframework.data.jpa.domain.Specification;

public class UserReviewsSpecification {
  public static Specification<UserReviewsModel> hasUser(UserModel user) {
    return (root, query, criteriaBuilder) ->
        user == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("user"), user);
  }
  public static Specification<UserReviewsModel> hasProduct(ProductModel product) {
    return (root, query, criteriaBuilder) ->
        product == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("product"), product);
  }

}

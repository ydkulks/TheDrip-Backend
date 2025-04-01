package dev.ydkulks.TheDrip.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.models.UserReviewsModel;
import dev.ydkulks.TheDrip.models.UserReviewsSpecification;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.UserRepo;
import dev.ydkulks.TheDrip.repos.UserReviewsRepository;
import jakarta.transaction.Transactional;

@Service
public class UserReviewsService {
  @Autowired private UserReviewsRepository userReviewsRepository;
  @Autowired private UserRepo userRepo;
  @Autowired private ProductRepository productRepository;

  @Transactional
  public UserReviewsModel createOrUpdateReview(
      Integer user,
      Integer product,
      String reviewTitle,
      String reviewText,
      Integer rating
      ) {
    UserModel userObj = userRepo
      .findById(user)
      .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + user));
    ProductModel productObj = productRepository
      .findById(product)
      .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + product));

    Optional<UserReviewsModel> reviewStatus = userReviewsRepository.findByUser_IdAndProduct_ProductId(user, product);

    UserReviewsModel review;
    if (reviewStatus.isEmpty()) {
      // If review does not exists, create a new one
      review = new UserReviewsModel();
    } else {
      // Update if review exists
      review = reviewStatus.get();
    }

    review.setUser(userObj);
    review.setProduct(productObj);
    review.setReviewTitle(reviewTitle);
    review.setReviewText(reviewText);
    review.setRating(rating);
    return userReviewsRepository.save(review);
  }

  @Transactional
  public Page<UserReviewsModel> getReview(
      UserModel user,
      ProductModel product,
      String sortBy,
      String sortDirection,
      Pageable pageable
      ){
    Specification<UserReviewsModel> spec =
        Specification.where(UserReviewsSpecification.hasProduct(product))
        .and(UserReviewsSpecification.hasUser(user));

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
    Page<UserReviewsModel> reviews = userReviewsRepository.findAll(spec, sortedPageable);
    return new PageImpl<>(reviews.toList(), sortedPageable, reviews.getTotalElements());
  }

  @Transactional
  public Page<UserReviewsModel> getProductReviews(
      // UserModel user,
      ProductModel product,
      String sortBy,
      String sortDirection,
      Pageable pageable
      ){
    Specification<UserReviewsModel> spec =
        Specification.where(UserReviewsSpecification.hasProduct(product));
        // .and(UserReviewsSpecification.hasUser(user));

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
    Page<UserReviewsModel> reviews = userReviewsRepository.findAll(spec, sortedPageable);
    return new PageImpl<>(reviews.toList(), sortedPageable, reviews.getTotalElements());
  }
}

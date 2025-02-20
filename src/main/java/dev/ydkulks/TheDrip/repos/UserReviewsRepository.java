package dev.ydkulks.TheDrip.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.UserReviewsModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface UserReviewsRepository extends JpaRepository<UserReviewsModel, Integer>, JpaSpecificationExecutor<UserReviewsModel> {
  Optional<UserReviewsModel> findByUser_IdAndProduct_ProductId(Integer userId, Integer productId);
}

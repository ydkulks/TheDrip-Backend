package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.UserReviewsModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface UserReviewsRepository extends JpaRepository<UserReviewsModel, Integer> {
}

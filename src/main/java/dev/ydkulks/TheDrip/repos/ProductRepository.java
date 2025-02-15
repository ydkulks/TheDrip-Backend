package dev.ydkulks.TheDrip.repos;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductCreationModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductRepository extends JpaRepository<ProductCreationModel, Integer> {
  Optional<ProductCreationModel> findByProductName(String productName);
  Optional<ProductCreationModel> findByProductId(Integer productId);
  Page<ProductCreationModel> findAll(Pageable pageable);
}

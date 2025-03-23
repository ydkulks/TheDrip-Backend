package dev.ydkulks.TheDrip.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Integer>, JpaSpecificationExecutor<ProductModel> {
  Optional<ProductModel> findByProductName(String productName);
  Optional<ProductModel> findByProductId(Integer productId);
  Page<ProductModel> findByProductIdIn(List<Integer> productIds, Pageable pageable);
  List<ProductModel> findByProductIdIn(List<Integer> productIds);
  Page<ProductModel> findAll(Pageable pageable);
}

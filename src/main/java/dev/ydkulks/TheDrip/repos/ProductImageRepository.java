package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductImageModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageModel, Integer> {
}

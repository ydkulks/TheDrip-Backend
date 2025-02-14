package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductSizesModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductSizesRepository extends JpaRepository<ProductSizesModel, Integer> {
}

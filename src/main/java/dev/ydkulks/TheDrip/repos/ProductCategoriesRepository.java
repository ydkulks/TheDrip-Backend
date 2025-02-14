package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductCategoriesModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductCategoriesRepository extends JpaRepository<ProductCategoriesModel, Integer> {
}

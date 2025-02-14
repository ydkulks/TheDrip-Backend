package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductColorsModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductColorsRepository extends JpaRepository<ProductColorsModel, Integer> {
}

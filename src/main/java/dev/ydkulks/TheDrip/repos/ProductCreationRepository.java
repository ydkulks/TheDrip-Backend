package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductCreationModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductCreationRepository extends JpaRepository<ProductCreationModel, Integer> {
}

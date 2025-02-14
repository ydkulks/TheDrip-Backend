package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductProductColorsModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductProductColorsRepository extends JpaRepository<ProductProductColorsModel, Integer> {
}

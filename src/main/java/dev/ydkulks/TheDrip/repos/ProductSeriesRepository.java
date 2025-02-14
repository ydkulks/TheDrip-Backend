package dev.ydkulks.TheDrip.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductSeriesModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductSeriesRepository extends JpaRepository<ProductSeriesModel, Integer> {
}

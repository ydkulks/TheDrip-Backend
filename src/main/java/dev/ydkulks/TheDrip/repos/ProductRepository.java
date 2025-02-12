package dev.ydkulks.TheDrip.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.ydkulks.TheDrip.models.ProductModel;

// JpaRepository<TableModel, PrimaryKeyType>
@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Integer> {
  @Query(value = """
        SELECT 
            p.product_id AS productId,
            p.product_name AS productName,
            p.product_description AS productDescription,
            p.product_price AS productPrice,
            p.product_stock AS productStock,
            pseries.series_name AS seriesName,
            c.category_name AS categoryName,
            u.username AS sellerName,
            ARRAY_AGG(DISTINCT ps.size_name) AS sizes,
            ARRAY_AGG(DISTINCT pc.color_name) AS colors,
            ARRAY_AGG(DISTINCT pi.img_path) AS images
        FROM 
            product p
        LEFT JOIN 
            product_series pseries ON p.series_id = pseries.series_id
        LEFT JOIN 
            categories c ON p.category_id = c.category_id
        LEFT JOIN 
            users u ON p.user_id = u.id
        LEFT JOIN 
            product_product_sizes pps ON p.product_id = pps.product_id
        LEFT JOIN 
            product_sizes ps ON pps.size_id = ps.size_id
        LEFT JOIN 
            product_product_colors ppc ON p.product_id = ppc.product_id
        LEFT JOIN 
            product_colors pc ON ppc.color_id = pc.color_id
        LEFT JOIN 
            product_product_images ppi ON p.product_id = ppi.product_id
        LEFT JOIN 
            product_images pi ON ppi.img_id = pi.img_id
        GROUP BY 
            p.product_id, pseries.series_name, c.category_name, u.username
        """, nativeQuery = true)
    Page<ProductProjectionDTO> getAllProducts(Pageable pageable);
}

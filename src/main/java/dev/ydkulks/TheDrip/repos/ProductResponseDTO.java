package dev.ydkulks.TheDrip.repos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDTO {
    private Long productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private Integer productStock;
    private String seriesName;
    private String categoryName;
    private String sellerName;
    private List<String> sizes;
    private List<String> colors;
    private List<String> images; // Presigned URLs

    // Constructor to map from ProductProjection
    public ProductResponseDTO(ProductProjectionDTO projection, List<String> images) {
        this.productId = projection.getProductId();
        this.productName = projection.getProductName();
        this.productDescription = projection.getProductDescription();
        this.productPrice = projection.getProductPrice();
        this.productStock = projection.getProductStock();
        this.seriesName = projection.getSeriesName();
        this.categoryName = projection.getCategoryName();
        this.sellerName = projection.getSellerName();
        this.sizes = projection.getSizes();
        this.colors = projection.getColors();
        this.images = images;
    }
}


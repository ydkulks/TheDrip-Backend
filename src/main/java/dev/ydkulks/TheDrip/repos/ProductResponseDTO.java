package dev.ydkulks.TheDrip.repos;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import dev.ydkulks.TheDrip.models.ProductColorsModel;
import dev.ydkulks.TheDrip.models.ProductCreationModel;
import dev.ydkulks.TheDrip.models.ProductSizesModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDTO {
    private Integer productId;
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
    // Optional<ProductCreationModel>
    // public ProductResponseDTO(ProductProjectionDTO projection, List<String> images) {
    public ProductResponseDTO(Optional<ProductCreationModel> product, List<String> images) {
        this.productId = product.get().getProductId();
        this.productName = product.get().getProductName();
        this.productDescription = product.get().getProductDescription();
        this.productPrice = product.get().getProductPrice();
        this.productStock = product.get().getProductStock();
        this.seriesName = product.get().getSeries().getSeries_name();
        this.categoryName = product.get().getCategory().getCategory_name();
        this.sellerName = product.get().getUser().getUsername();
        this.sizes = product.get().getSizes()
          .stream()
          .map(ProductSizesModel::getSize_name)
          .collect(Collectors.toList());
        this.colors = product.get().getColors()
          .stream()
          .map(ProductColorsModel::getColor_name)
          .collect(Collectors.toList());
        this.images = images;
    }
}


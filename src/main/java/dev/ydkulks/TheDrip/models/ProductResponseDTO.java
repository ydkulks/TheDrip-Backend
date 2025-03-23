package dev.ydkulks.TheDrip.models;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private Integer productSold;
    private String seriesName;
    private String categoryName;
    private String sellerName;
    private List<String> sizes;
    private List<String> colors;
    private List<String> images; // Presigned URLs

    public ProductResponseDTO(Optional<ProductModel> product, List<String> images) {
        this.productId = product.get().getProductId();
        this.productName = product.get().getProductName();
        this.productDescription = product.get().getProductDescription();
        this.productPrice = product.get().getProductPrice();
        this.productStock = product.get().getProductStock();
        this.productSold = product.get().getProductSold();
        this.seriesName = product.get().getSeries().getSeriesName();
        this.categoryName = product.get().getCategory().getCategoryName();
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

    public ProductResponseDTO(ProductModel product, List<String> images) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.productDescription = product.getProductDescription();
        this.productPrice = product.getProductPrice();
        this.productStock = product.getProductStock();
        this.productSold = product.getProductSold();
        this.seriesName = product.getSeries().getSeriesName();
        this.categoryName = product.getCategory().getCategoryName();
        this.sellerName = product.getUser().getUsername();
        this.sizes = product.getSizes()
          .stream()
          .map(ProductSizesModel::getSize_name)
          .collect(Collectors.toList());
        this.colors = product.getColors()
          .stream()
          .map(ProductColorsModel::getColor_name)
          .collect(Collectors.toList());
        this.images = images;
    }
}


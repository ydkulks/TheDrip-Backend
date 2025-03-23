package dev.ydkulks.TheDrip.models;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListProductResponseDTO {
    private Integer productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private Integer productStock;
    // private Integer productSold;
    private Integer seriesId;
    private Integer categoryId;
    private Integer userId;
    private List<Integer> productSizes;
    private List<Integer> productColors;
    // private List<String> images; // Presigned URLs

    public ListProductResponseDTO(ProductModel product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.productDescription = product.getProductDescription();
        this.productPrice = product.getProductPrice();
        this.productStock = product.getProductStock();
        // this.productSold = product.getProductSold();
        this.seriesId = product.getSeries().getSeries_id();
        this.categoryId = product.getCategory().getCategoryId();
        this.userId = product.getUser().getId();
        this.productSizes = product.getSizes()
          .stream()
          .map(ProductSizesModel::getSize_id)
          .collect(Collectors.toList());
        this.productColors = product.getColors()
          .stream()
          .map(ProductColorsModel::getColor_id)
          .collect(Collectors.toList());
        // this.images = images;
    }
}

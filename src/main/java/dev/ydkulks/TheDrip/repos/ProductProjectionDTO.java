package dev.ydkulks.TheDrip.repos;

import java.util.List;

public interface ProductProjectionDTO {
    Long getProductId();
    String getProductName();
    String getProductDescription();
    Double getProductPrice();
    Integer getProductStock();
    String getSeriesName();
    String getCategoryName();
    String getSellerName();
    List<String> getSizes();
    List<String> getColors();
    List<String> getImages();
}

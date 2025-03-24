package dev.ydkulks.TheDrip.models;
import java.util.List;

public class ImageDeleteRequest {
  private List<Integer> productIds;
  private List<String> images;

  public List<Integer> getProductIds() {
    return productIds;
  }

  public void setProductIds(List<Integer> productIds) {
    this.productIds = productIds;
  }

  public List<String> getImages() {
    return images;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }
}

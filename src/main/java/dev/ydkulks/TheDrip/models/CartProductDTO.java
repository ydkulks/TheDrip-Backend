package dev.ydkulks.TheDrip.models;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartProductDTO {
  private Integer productId;
  private String productName;
  private String category;
  private String series;
  private String image;
  // private Set<ProductImageModel> images;
}

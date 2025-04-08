package dev.ydkulks.TheDrip.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutProduct {
  private String successUrl;
  private String cancelUrl;
  private List<ProductItem> products;
  private List<Integer> cartItemsId;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ProductItem {
    private Integer productId;
    private Long qty;
  }
}

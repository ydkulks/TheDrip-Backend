package dev.ydkulks.TheDrip.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemsDTO {
  private Integer cart_items_id;
  private CartDTO cart;
  private CartProductDTO product;
  private Integer quantity;
  private String color;
  private String size;
}

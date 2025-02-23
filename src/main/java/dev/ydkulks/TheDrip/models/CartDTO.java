
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
public class CartDTO {
  private Integer cart_id;
  private String user;
  // private List<CartItemsDTO> cartItems;
}

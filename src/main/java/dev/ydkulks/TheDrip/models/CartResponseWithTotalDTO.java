package dev.ydkulks.TheDrip.models;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseWithTotalDTO {
  private Page<CartItemsModel> cartItemsPage;
  private double total;
}

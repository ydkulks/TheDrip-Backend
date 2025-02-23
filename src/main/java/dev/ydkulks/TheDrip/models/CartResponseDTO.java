package dev.ydkulks.TheDrip.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResponseDTO {
  private List<CartItemsDTO> content;
  private CartPageDTO page;
}

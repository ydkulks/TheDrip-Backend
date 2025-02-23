package dev.ydkulks.TheDrip.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartPageDTO {
  private int size;
  private int number;
  private long totalElements;
  private int totalPages;
}

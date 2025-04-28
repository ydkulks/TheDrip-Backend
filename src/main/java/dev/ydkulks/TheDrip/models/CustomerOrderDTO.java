package dev.ydkulks.TheDrip.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderDTO {
  private Integer userId;
  private Integer productId;
  private Integer quantity;
  private BigDecimal orderAmount;
  private String orderStatus;
  private String productName;
  private String category;
  private String series;
}

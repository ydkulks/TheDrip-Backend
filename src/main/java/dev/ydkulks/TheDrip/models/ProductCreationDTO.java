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
public class ProductCreationDTO {
    private String productName;
    private Integer categoryId;
    private Integer userId;
    private Integer seriesId;
    private Double productPrice;
    private String productDescription;
    private Integer productStock;
    private Integer productSold;
    private List<Integer> productSizes;
    private List<Integer> productColors;
}

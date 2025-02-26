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
public class CategorySeriesSizesColorsDTO {
  private List<ProductCategoriesModel> categories;
  private List<ProductSeriesModel> series;
  private List<ProductSizesModel> sizes;
  private List<ProductColorsModel> colors;
}

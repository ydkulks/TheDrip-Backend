package dev.ydkulks.TheDrip.models;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartMapper {
  CartMapper INSTANCE = Mappers.getMapper( CartMapper.class );

  @Mapping(source = "cartItemsId", target = "cart_items_id")
  @Mapping(source = "cart", target = "cart")
  @Mapping(source = "product", target = "product")
  CartItemsDTO cartItemsModelToCartItemDTO(CartItemsModel cartItemsModel);

  @Mapping(source = "cart_id", target = "cart_id")
  @Mapping(source = "user.username", target = "user")
  // @Mapping(source = "cartItems", target = "cartItems")
  CartDTO cartModelToCartDTO(CartModel cartModel);

  @Mapping(source = "productId", target = "productId")
  @Mapping(source = "productName", target = "productName")
  @Mapping(source = "category.categoryName", target = "category")
  @Mapping(source = "series.seriesName", target = "series")
  // @Mapping(source = "images", target = "image", qualifiedByName = "mapFirstImage")
  // @Mapping(source = "images", target = "images")
  CartProductDTO productModelToProductDTO(ProductModel productModel);

  // default String mapFirstImage(List<String> images) {
  //   return images != null && !images.isEmpty() ? images.get(0) : null;
  // }
}

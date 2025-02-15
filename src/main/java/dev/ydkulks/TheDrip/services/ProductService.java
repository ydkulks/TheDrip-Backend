package dev.ydkulks.TheDrip.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.ProductCategoriesModel;
import dev.ydkulks.TheDrip.models.ProductColorsModel;
import dev.ydkulks.TheDrip.models.ProductCreationModel;
import dev.ydkulks.TheDrip.models.ProductImageModel;
import dev.ydkulks.TheDrip.models.ProductProductColorsModel;
import dev.ydkulks.TheDrip.models.ProductProductSizesModel;
import dev.ydkulks.TheDrip.models.ProductSeriesModel;
import dev.ydkulks.TheDrip.models.ProductSizesModel;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.repos.ProductCategoriesRepository;
import dev.ydkulks.TheDrip.repos.ProductColorsRepository;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.ProductProductColorsRepository;
import dev.ydkulks.TheDrip.repos.ProductProductSizesRepository;
import dev.ydkulks.TheDrip.repos.ProductResponseDTO;
import dev.ydkulks.TheDrip.repos.ProductSeriesRepository;
import dev.ydkulks.TheDrip.repos.ProductSizesRepository;
import dev.ydkulks.TheDrip.repos.UserRepo;
import jakarta.transaction.Transactional;


@Service
public class ProductService {
  @Autowired ProductColorsRepository productColorsRepository;
  @Autowired ProductSeriesRepository productSeriesRepository;
  @Autowired ProductProductColorsRepository productProductColorsRepository;
  @Autowired ProductProductSizesRepository productProductSizesRepository;
  @Autowired ProductImageService productImageService;
  @Autowired ProductCategoriesRepository productCategoriesRepository;
  @Autowired UserRepo userRepo;
  @Autowired ProductSizesRepository productSizesRepository;
  @Autowired ProductRepository productRepository;

  // NOTE: Create
  @Transactional
  public ProductCreationModel createOrUpdateProduct(
    String productName,
    Integer categoryId,
    Integer userId,
    Integer seriesId,
    Double productPrice,
    String productDescription,
    Integer productStock,
    List<Integer> sizeIds,
    List<Integer> colorIds
  ) {
    // Check if product exists
    Optional<ProductCreationModel> existingProductOpt = productRepository.findByProductName(productName);
    ProductCategoriesModel category = productCategoriesRepository
      .findById(categoryId)
      .orElseThrow(() ->
        new IllegalArgumentException("Invalid category ID: " + categoryId)
      );
    UserModel user = userRepo
      .findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
    ProductSeriesModel series = productSeriesRepository
      .findById(seriesId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid series ID: " + seriesId));

    ProductCreationModel product;
    if (existingProductOpt.isPresent()) {
      product = existingProductOpt.get();
      System.out.println("Updating existing product: " + product.getProductId());
    } else {
      product = new ProductCreationModel();
      System.out.println("Creating new product");
    }

    product.setProductName(productName);
    product.setCategory(category);
    product.setUser(user);
    product.setSeries(series);
    product.setProductPrice(productPrice);
    product.setProductDescription(productDescription);
    product.setProductStock(productStock);

    // Handle sizes
    Set<ProductSizesModel> sizes = new HashSet<>();
    for (Integer sizeId : sizeIds) {
      ProductSizesModel size = productSizesRepository
        .findById(sizeId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid size ID: " + sizeId));
      sizes.add(size);
    }
    product.setSizes(sizes);

    // Handle colors
    Set<ProductColorsModel> colors = new HashSet<>();
    for (Integer colorId : colorIds) {
      ProductColorsModel color = productColorsRepository
        .findById(colorId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid color ID: " + colorId));
      colors.add(color);
    }
    product.setColors(colors);

    return productRepository.save(product);
  }

  @Transactional
  public ProductColorsModel createOrUpdateColor(ProductColorsModel newColor) {
    return productColorsRepository.save(newColor);
  }

  @Transactional
  public ProductSeriesModel createOrUpdateSeries(ProductSeriesModel newSeries) {
    return productSeriesRepository.save(newSeries);
  }

  @Transactional
  public ProductProductColorsModel linkOrUpdateLinkOfColor(ProductProductColorsModel newColorLink) {
    return productProductColorsRepository.save(newColorLink);
  }

  @Transactional
  public ProductProductSizesModel linkOrUpdateLinkOfSize(ProductProductSizesModel newColorLink) {
    return productProductSizesRepository.save(newColorLink);
  }

  // NOTE: Get
  @Transactional
  public ProductResponseDTO getProductDetails(int id) {
    Optional<ProductCreationModel> product = productRepository.findByProductId(id);
    if(product.isPresent()){
      List<String> s3Paths = product.get().getImages()
        .stream()
        .map(ProductImageModel::getImg_path)
        .collect(Collectors.toList());

      List<String> imageUrls = s3Paths
        .stream()
        .map(path -> productImageService.getPresignedImageURL("thedrip", path))
        .collect(Collectors.toList());
      return new ProductResponseDTO(product, imageUrls);
    }

    return null;
  }

  @Transactional
  public CompletableFuture<List<ProductResponseDTO>> getAllProductDetails(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ProductCreationModel> products = productRepository.findAll(pageable);

    // Check if the page is valid
    if (page >= products.getTotalPages()) {
      return CompletableFuture.completedFuture(Collections.emptyList());
    }

    List<ProductResponseDTO> productResponseDTOs = products.getContent().stream()
      .map(product -> {
        List<String> s3Paths = product.getImages()
          .stream()
          .map(ProductImageModel::getImg_path)
          .collect(Collectors.toList());

        List<String> imageUrls = s3Paths
          .stream()
          .map(path -> productImageService.getPresignedImageURL("thedrip", path))
          .collect(Collectors.toList());

        return new ProductResponseDTO(Optional.of(product), imageUrls);
      })
    .collect(Collectors.toList());

    return CompletableFuture.completedFuture(productResponseDTOs);
  }
}

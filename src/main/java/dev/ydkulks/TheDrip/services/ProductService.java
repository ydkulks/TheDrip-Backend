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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.CategorySeriesSizesColorsDTO;
import dev.ydkulks.TheDrip.models.ProductCategoriesModel;
import dev.ydkulks.TheDrip.models.ProductColorsModel;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.ProductImageModel;
import dev.ydkulks.TheDrip.models.ProductProductColorsModel;
import dev.ydkulks.TheDrip.models.ProductProductSizesModel;
import dev.ydkulks.TheDrip.models.ProductSeriesModel;
import dev.ydkulks.TheDrip.models.ProductSizesModel;
import dev.ydkulks.TheDrip.models.ProductSpecification;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.repos.ProductCategoriesRepository;
import dev.ydkulks.TheDrip.repos.ProductColorsRepository;
import dev.ydkulks.TheDrip.repos.ProductImageRepository;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.ProductProductColorsRepository;
import dev.ydkulks.TheDrip.repos.ProductProductSizesRepository;
import dev.ydkulks.TheDrip.models.ProductResponseDTO;
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
  @Autowired ProductImageRepository productImageRepository;

  // NOTE: Create
  @Transactional
  public ProductModel createOrUpdateProduct(
    String productName,
    Integer categoryId,
    Integer userId,
    Integer seriesId,
    Double productPrice,
    String productDescription,
    Integer productStock,
    Integer productSold,
    List<Integer> sizeIds,
    List<Integer> colorIds
  ) {
    // Check if product exists
    Optional<ProductModel> existingProductOpt = productRepository.findByProductName(productName);
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

    ProductModel product;
    if (existingProductOpt.isPresent()) {
      product = existingProductOpt.get();
      System.out.println("Updating existing product: " + product.getProductId());
    } else {
      product = new ProductModel();
      System.out.println("Creating new product");
    }

    product.setProductName(productName);
    product.setCategory(category);
    product.setUser(user);
    product.setSeries(series);
    product.setProductPrice(productPrice);
    product.setProductDescription(productDescription);
    product.setProductStock(productStock);
    product.setProductSold(productSold);

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
  public ProductSeriesModel createOrUpdateSeries(String seriesName) {
    Optional<ProductSeriesModel> existingSeries = productSeriesRepository .findBySeriesName(seriesName);
    ProductSeriesModel series;
    if (existingSeries.isPresent()) {
      series = existingSeries.get();
    } else {
      series = new ProductSeriesModel();
    }
    series.setSeriesName(seriesName);
    return productSeriesRepository.save(series);
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
    Optional<ProductModel> product = productRepository.findByProductId(id);
    if(product.isPresent()){
      List<String> s3Paths = product.get().getImages()
        .stream()
        .map(ProductImageModel::getImgPath)
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
    Page<ProductModel> products = productRepository.findAll(pageable);

    // Check if the page is valid
    if (page >= products.getTotalPages()) {
      return CompletableFuture.completedFuture(Collections.emptyList());
    }

    List<ProductResponseDTO> productResponseDTOs = products.getContent().stream()
      .map(product -> {
        List<String> s3Paths = product.getImages()
          .stream()
          .map(ProductImageModel::getImgPath)
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

  @Transactional
  public Page<ProductResponseDTO> getProductsByFilters(
      ProductCategoriesModel category,
      UserModel user,
      ProductSeriesModel series,
      Double minPrice,
      Double maxPrice,
      String sortBy,
      String sortDirection,
      String searchTerm,
      Pageable pageable) {

    Specification<ProductModel> spec =
        Specification.where(ProductSpecification.hasCategory(category))
        .and(ProductSpecification.hasUser(user))
        .and(ProductSpecification.hasSeries(series))
        .and(ProductSpecification.hasSearchTerm(searchTerm))
        .and(ProductSpecification.hasPriceBetween(minPrice, maxPrice));

    Sort sort = null;
    if (sortBy != null && !sortBy.isEmpty()) {
      Sort.Direction direction =
        sortDirection != null && sortDirection.equalsIgnoreCase("desc")
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
      sort = Sort.by(direction, sortBy);
    }

    // Create a new Pageable object with the Sort information
    Pageable sortedPageable = pageable;
    if (sort != null) {
      sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    Page<ProductModel> productModelPage = productRepository.findAll(spec, sortedPageable);

    List<ProductResponseDTO> productResponseDTOList =
      productModelPage.getContent().stream()
      .map(
          product -> {
            List<String> s3Paths =
              product.getImages().stream()
              .map(ProductImageModel::getImgPath)
              .collect(Collectors.toList());

            List<String> imageUrls =
              s3Paths.stream()
              .map(
                  path ->
                  productImageService.getPresignedImageURL(
                    "thedrip", path))
              .collect(Collectors.toList());

            return new ProductResponseDTO(Optional.of(product), imageUrls);
          })
    .collect(Collectors.toList());

    return new PageImpl<>(productResponseDTOList, sortedPageable, productModelPage.getTotalElements());
  }

  @Transactional
  public CategorySeriesSizesColorsDTO getCategorySeriesSizesColors() {
    List<ProductCategoriesModel> categores = productCategoriesRepository.findAll();
    List<ProductSeriesModel> series = productSeriesRepository.findAll();
    List<ProductSizesModel> sizes= productSizesRepository.findAll();
    List<ProductColorsModel> colors= productColorsRepository.findAll();

    CategorySeriesSizesColorsDTO dto = new CategorySeriesSizesColorsDTO();
    dto.setCategories(categores);
    dto.setSeries(series);
    dto.setSizes(sizes);
    dto.setColors(colors);
    return dto;
  }

  // NOTE: Delete
  @Transactional
  public Optional<ProductModel> deleteProduct(Integer productId) {
    ProductModel product = productRepository.findById(productId).orElseThrow(() ->
        new IllegalArgumentException("Invalid product ID: " + productId)
      );
    List<String> urls = productImageService.deleteImagesForProduct(
        "thedrip",
        product.getUser().getUsername(),
        product.getProductId()
      ).join();
    if(urls != null && !urls.isEmpty()){
      List<ProductImageModel> imagesToDelete = productImageRepository.findByImgPathIn(urls);
      productImageRepository.deleteAll(imagesToDelete);
      productRepository.delete(product);
    }
    return null;
  }
}

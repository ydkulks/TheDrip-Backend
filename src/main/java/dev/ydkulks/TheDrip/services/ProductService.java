package dev.ydkulks.TheDrip.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import dev.ydkulks.TheDrip.models.CategorySeriesSizesColorsDTO;
import dev.ydkulks.TheDrip.models.ListProductResponseDTO;
import dev.ydkulks.TheDrip.models.ProductCategoriesModel;
import dev.ydkulks.TheDrip.models.ProductColorsModel;
import dev.ydkulks.TheDrip.models.ProductCreationDTO;
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

  Logger logger = LoggerFactory.getLogger(this.getClass());
  // NOTE: Create
  @Transactional
    public List<ProductModel> createProducts(List<ProductCreationDTO> productDataList) {
        List<ProductModel> createdProducts = new ArrayList<>();
        for (ProductCreationDTO productData : productDataList) {
            ProductModel product = new ProductModel();
            logger.info("Creating new product");
            createdProducts.add(setProductDetails(product, productData.getProductName(), productData.getCategoryId(), productData.getUserId(), productData.getSeriesId(), productData.getProductPrice(), productData.getProductDescription(), productData.getProductStock(), productData.getProductSold(), productData.getProductSizes(), productData.getProductColors()));
        }
        return createdProducts;
    }

  // NOTE: Update
  @Transactional
  public List<ProductModel> updateProducts( 
      @RequestParam List<Integer> productIds, 
      List<ProductCreationDTO> productUpdateDataList) {
    if (productIds.size() != productUpdateDataList.size()) {
      throw new IllegalArgumentException("Number of product IDs and update data entries must match.");
    }

    List<ProductModel> updatedProducts = new ArrayList<>();
    for (int i = 0; i < productIds.size(); i++) {
      Integer productId = productIds.get(i);
      ProductCreationDTO updateData = productUpdateDataList.get(i);

      Optional<ProductModel> existingProductOpt = productRepository.findById(productId);
      ProductModel product = existingProductOpt.orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found."));

      logger.info("Updating existing product: ", product.getProductId());

      updatedProducts.add(setProductDetails(product, updateData.getProductName(), updateData.getCategoryId(), updateData.getUserId(), updateData.getSeriesId(), updateData.getProductPrice(), updateData.getProductDescription(), updateData.getProductStock(), updateData.getProductSold(), updateData.getProductSizes(), updateData.getProductColors()));
    }
    return updatedProducts;
  }

  private ProductModel setProductDetails(
      ProductModel product,
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
      ProductCategoriesModel category = productCategoriesRepository
        .findById(categoryId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
      UserModel user = userRepo
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
      ProductSeriesModel series = productSeriesRepository
        .findById(seriesId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid series ID: " + seriesId));

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
  public Page<ProductResponseDTO> getProductByIds(List<Integer> id, Pageable pageable) {
    Page<ProductModel> products = productRepository.findByProductIdIn(id, pageable);
    return products.map(product -> {
        List<String> s3Paths = product.getImages()
            .stream()
            .map(ProductImageModel::getImgPath)
            .collect(Collectors.toList());

        List<String> imageUrls = s3Paths
            .stream()
            .map(path -> productImageService.getPresignedImageURL("thedrip", path))
            .collect(Collectors.toList());

        return new ProductResponseDTO(product, imageUrls);
    });
  }

  @Transactional
  public List<ListProductResponseDTO> getAllProductByIdsNoImg(List<Integer> id) {
    List<ProductModel> products = productRepository.findByProductIdIn(id);
    // return products;
    return products.stream()
      .map(
          product -> {
            // Create a ListProductResponseDTO from the ProductModel
            return new ListProductResponseDTO(product); // Assuming you have a constructor that accepts ProductModel
          })
      .collect(Collectors.toList());
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
      List<ProductColorsModel> colors,
      List<ProductSizesModel> sizes,
      Boolean inStock,
      List<ProductCategoriesModel> categories,
      UserModel user,
      List<ProductSeriesModel> series,
      Double minPrice,
      Double maxPrice,
      String sortBy,
      String sortDirection,
      String searchTerm,
      Integer imgCount,
      Pageable pageable) {

    Specification<ProductModel> spec =
        Specification.where(ProductSpecification.hasColorsIn(colors))
        .and(ProductSpecification.hasSizesIn(sizes))
        .and(ProductSpecification.hasProductStock(inStock))
        .and(ProductSpecification.hasCategoryIn(categories))
        .and(ProductSpecification.hasUser(user))
        .and(ProductSpecification.hasSeriesIn(series))
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
              .limit(imgCount)
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
  public void deleteProducts(List<Integer> productIds) {
    List<ProductModel> productsToDelete = productRepository.findAllById(productIds);

    if (productsToDelete.size() != productIds.size()) {
      // Handle the case where some product IDs were not found
      List<Integer> foundProductIds = productsToDelete.stream()
        .map(ProductModel::getProductId)
        .collect(Collectors.toList());

      List<Integer> notFoundProductIds = productIds.stream()
        .filter(id -> !foundProductIds.contains(id))
        .collect(Collectors.toList());

      throw new IllegalArgumentException(
          "Invalid product IDs: " + notFoundProductIds
          );
    }

    for (ProductModel product : productsToDelete) {
      try {
        // Ensure images are deleted *before* deleting the product
        productImageService.deleteImagesForProduct(
            "thedrip",
            product.getUser().getUsername(),
            product.getProductId()
            ).join();

        // Now it's safe to delete the product
        productRepository.delete(product);
        logger.info("Product ID {} deleted successfully.", product.getProductId());

      } catch (Exception e) {
        // If image deletion fails, *do not* delete the product
        logger.error(
            "Error deleting images for product ID {}. Product will NOT be deleted. {}",
            product.getProductId(),
            e.getMessage(),
            e
            );
        // Consider re-throwing the exception or handling it in a way that
        // prevents the transaction from committing if image deletion is critical.
        throw new RuntimeException(
            "Failed to delete images for product ID " + product.getProductId() +
            ". Product deletion aborted.",
            e
            ); //Re-throwing to rollback transaction
      }
    }

  }
}

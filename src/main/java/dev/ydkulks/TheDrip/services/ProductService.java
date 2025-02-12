package dev.ydkulks.TheDrip.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.repos.ProductProjectionDTO;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.ProductResponseDTO;


@Service
public class ProductService {
  @Autowired
  ProductRepository repo;

  @Autowired
  ProductImageService productImageService;

  public Optional<ProductModel> getProductDetails() {
    Optional<ProductModel> product = repo.findById(1);
    // List<ProductModel> product = repo.findAll();
    return product;
  }

  // TODO: Paginate and sort all products
  public CompletableFuture<List<ProductResponseDTO>> getAllProductDetails(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    // Fetch the products with pagination
    Page<ProductProjectionDTO> products = repo.getAllProducts(pageable);

    // Check if the page is valid
    if (page >= products.getTotalPages()) {
      return CompletableFuture.completedFuture(Collections.emptyList());
    }

    List<CompletableFuture<ProductResponseDTO>> futures = products.getContent().stream()
      .map(product -> {
        List<String> s3Paths = product.getImages(); // Get all stored paths

        // Fetch pre-signed URLs for each path
        List<CompletableFuture<List<String>>> urlFutures = s3Paths.stream()
          .map(path -> productImageService.getPresignedImageURLs("thedrip", path))
          .collect(Collectors.toList());

        // Combine all futures into a single future list of URLs
        return CompletableFuture.allOf(urlFutures.toArray(new CompletableFuture[0]))
          .thenApply(v -> {
            List<List<String>> urlLists = urlFutures.stream()
              .map(CompletableFuture::join)
              .collect(Collectors.toList());
            return urlLists.stream().flatMap(List::stream).collect(Collectors.toList());
          })
        .thenApply(imageUrls -> new ProductResponseDTO(product, imageUrls));
      })
    .collect(Collectors.toList());

    // Combine all product futures into a single future list of ProductResponseDTO
    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
      .thenApply(v -> {
        List<ProductResponseDTO> productResponseList = futures.stream()
          .map(CompletableFuture::join)
          .collect(Collectors.toList());
        return productResponseList;
      });
  }
}

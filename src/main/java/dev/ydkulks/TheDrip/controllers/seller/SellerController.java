package dev.ydkulks.TheDrip.controllers.seller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.ydkulks.TheDrip.repos.ProductImageRepository;
import dev.ydkulks.TheDrip.repos.ProductProductImageRepository;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.services.ProductImageService;
import dev.ydkulks.TheDrip.services.ProductService;
import jakarta.transaction.Transactional;
import dev.ydkulks.TheDrip.models.ProductCreationDTO;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.ProductImageModel;
import dev.ydkulks.TheDrip.models.ProductProductImageModel;
import dev.ydkulks.TheDrip.models.ProductSeriesModel;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@RestController
@RequestMapping("/seller")
public class SellerController {
  private static final Logger logger = LoggerFactory.getLogger(SellerController.class);

  @Autowired private ProductImageRepository productImageRepository;
  @Autowired private ProductImageService productImageService;
  @Autowired private ProductProductImageRepository productProductImageRepository;
  @Autowired private ProductService productService;
  @Autowired private ProductRepository productRepository;

  // NOTE: Create product details
  @PostMapping("/products")
  public ResponseEntity<List<String>> createProducts(@RequestBody List<ProductCreationDTO> dataList) {
    try {
      List<ProductCreationDTO> productCreationDataList = new ArrayList<>();
      for (ProductCreationDTO data : dataList) {
        productCreationDataList.add(new ProductCreationDTO(
            data.getProductName(),
            data.getCategoryId(),
            data.getUserId(),
            data.getSeriesId(),
            data.getProductPrice(),
            data.getProductDescription(),
            data.getProductStock(),
            data.getProductSold(),
            data.getProductSizes(),
            data.getProductColors()));
      }

      List<ProductModel> createdProducts = productService.createProducts(productCreationDataList);
      List<String> productNames = new ArrayList<>();
      for (ProductModel product : createdProducts) {
        productNames.add(product.getProductName());
      }

      return new ResponseEntity<>(productNames, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/{username}/{productId}/image")
  @Transactional
  public ResponseEntity<?> uploadImage(
      @PathVariable String username,
      @PathVariable String productId,
      @RequestParam("file") List<MultipartFile> files) throws IOException {
    try {
      // Does the product exists?
      boolean product = productRepository.existsById(Integer.parseInt(productId));
      long imgCount = productProductImageRepository.countByProductId(Integer.parseInt(productId));
      if (imgCount + files.size() > 5) {
        return new ResponseEntity<>(
            "Maximum number of images (" + 5 + ") reached for product " + productId,
            HttpStatus.NOT_ACCEPTABLE);
      }
      if (product) {
        // Upload image to S3
        List<CompletableFuture<PutObjectResponse>> responses = productImageService
            .uploadMultipleFilesAsync("thedrip", username, productId, files);
        CompletableFuture.allOf(responses.toArray(new CompletableFuture[0])).join();
      }

      // Store in product_images and product_product_images
      files.forEach(file -> {
        String imgPath = String.format("%s/%s/%s", username, productId, file.getOriginalFilename());
        // Check if image exists for that product
        Optional<ProductImageModel> img = productImageRepository.findByImgPath(imgPath);
        ProductImageModel image;
        ProductProductImageModel productImageLink;
        if (img.isEmpty()) {
          image = new ProductImageModel();
          image.setImg_name(file.getOriginalFilename());
          image.setImg_type(file.getContentType());
          image.setImgPath(String.format("%s/%s/%s", username, productId, file.getOriginalFilename()));
          ProductImageModel savedImage = productImageRepository.save(image);

          int imgId = savedImage.getImg_id();

          productImageLink = new ProductProductImageModel();
          productImageLink.setProductId(Integer.parseInt(productId));
          productImageLink.setImg_id(imgId);
          productProductImageRepository.save(productImageLink);
        } else {
          image = img.get();
          // Update fields if needed, for example:
          image.setImg_name(file.getOriginalFilename());
          image.setImg_type(file.getContentType());

          productImageRepository.save(image); // Save the updated image
        }
      });
      return new ResponseEntity<>("All files uploaded", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

  }

  @PostMapping("/{username}/product/images")
  @Transactional
  public ResponseEntity<?> bulkUploadImages(
      // @RequestParam List<MultipartFile> productFiles,
      // @RequestPart("productFiles") Map<String, List<MultipartFile>> productFiles,
      @RequestParam Map<String, MultipartFile> files,
      @RequestParam Map<String, String> productIds,
      @PathVariable String username) throws IOException {
    logger.info("bulkUploadImages called for username: {}", username); // Entry log
    Map<String, java.util.List<MultipartFile>> productFiles = new HashMap<>();

    try {
      for (String fileKey : files.keySet()) {
        if (fileKey.startsWith("file")) {
          String fileNumber = fileKey.substring(4);
          String productIdKey = "productId" + fileNumber;
          String productId = productIds.get(productIdKey);

          if (!productFiles.containsKey(productId)) {
            productFiles.put(productId, new java.util.ArrayList<>());
          }
          productFiles.get(productId).add(files.get(fileKey));
        }
      }
      // System.out.println(productFiles.entrySet());

      // if (productFiles == null || productFiles.isEmpty()) {
      // String errorMessage = "No product files provided in the request.";
      // logger.warn(errorMessage);
      // return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
      // }
      // productFiles.forEach(files -> {
      // System.out.println(files.getOriginalFilename());
      // });

      for (Map.Entry<String, List<MultipartFile>> entry : productFiles.entrySet()) {
        String productId = entry.getKey();
        List<MultipartFile> filesOfProduct = entry.getValue();

        // logger.info("Processing product ID: {} with {} files", productId, filesOfProduct.size());

        if (!productRepository.existsById(Integer.parseInt(productId))) {
          String errorMessage = "Product with ID " + productId + " not found.";
          logger.error(errorMessage);
          return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
        }

        long existingImgCount = productProductImageRepository.countByProductId(Integer.parseInt(productId));
        long totalImgCount = existingImgCount + filesOfProduct.size();

        if (totalImgCount > 5) {
          String errorMessage = "Maximum number of images (5) reached for product "
              + productId
              + ". Current count: "
              + existingImgCount
              + ", Attempted upload: "
              + filesOfProduct.size();
          logger.warn(errorMessage);
          return new ResponseEntity<>(errorMessage, HttpStatus.PAYLOAD_TOO_LARGE);
        }

        // Upload image to S3
        try {
          // logger.info("Starting S3 upload for product {}", productId);
          List<CompletableFuture<PutObjectResponse>> responses = productImageService.uploadMultipleFilesAsync("thedrip",
              username, productId, filesOfProduct);

          CompletableFuture<Void> allOf = CompletableFuture.allOf(responses.toArray(new CompletableFuture[0]));

          allOf.exceptionally(
              throwable -> {
                logger.error(
                    "Error during S3 upload for product {}:", productId, throwable); // Log the entire exception
                return null;
              });

          allOf.join();

          // logger.info("S3 uploads completed for product {}", productId);

        } catch (Exception e) {
          String errorMessage = "Error during S3 upload for product " + productId + ": " + e.getMessage();
          logger.error(errorMessage, e);
          return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Store in product_images and product_product_images
        filesOfProduct.forEach(
            file -> {
              String imgPath = String.format("%s/%s/%s", username, productId, file.getOriginalFilename());
              try {
                // logger.info("Saving image metadata to DB for product {}, file: {}", productId,
                //     file.getOriginalFilename());

                // Check if image exists for that product
                Optional<ProductImageModel> img = productImageRepository.findByImgPath(imgPath);
                ProductImageModel image;
                ProductProductImageModel productImageLink;

                if (img.isEmpty()) {
                  image = new ProductImageModel();
                  image.setImg_name(file.getOriginalFilename());
                  image.setImg_type(file.getContentType());
                  image.setImgPath(imgPath);
                  ProductImageModel savedImage = productImageRepository.save(image);

                  int imgId = savedImage.getImg_id();

                  productImageLink = new ProductProductImageModel();
                  productImageLink.setProductId(Integer.parseInt(productId));
                  productImageLink.setImg_id(imgId);
                  productProductImageRepository.save(productImageLink);
                  logger.info("New image saved for product {}, file: {}", productId, file.getOriginalFilename());

                } else {
                  image = img.get();
                  // Update fields if needed, for example:
                  image.setImg_name(file.getOriginalFilename());
                  image.setImg_type(file.getContentType());
                  productImageRepository.save(image); // Save the updated image
                  logger.info("Image updated for product {}, file: {}", productId, file.getOriginalFilename());
                }
              } catch (Exception e) {
                logger.error(
                    "Error saving image metadata to DB for product {}:",
                    productId,
                    e); // Log the entire exception

                throw new RuntimeException(
                    "Failed to save image metadata for product " + productId,
                    e); // Terminate processing if DB save fails
              }
            });
        // logger.info("Database updates completed for product {}", productId);
      }
      logger.info("Bulk image upload successful for all products.");
      return ResponseEntity.ok("Bulk image upload successful.");

    } catch (NumberFormatException e) {
      String errorMessage = "Invalid product ID format in request.";
      logger.error(errorMessage, e);
      return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      String errorMessage = "An error occurred during bulk image upload: " + e.getMessage();
      logger.error(errorMessage, e);
      return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/series")
  @Transactional
  public ResponseEntity<?> createOrUpdateSeries(@RequestParam String seriesName) {
    try {
      ProductSeriesModel series = productService.createOrUpdateSeries(seriesName);
      return new ResponseEntity<>(series, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  // NOTE: Get presigned URL for 1 user ,1 of their product and 1 image
  @GetMapping("/{username}/{productId}/{img_name}")
  public String getImageLink(@PathVariable String username, @PathVariable String productId,
      @PathVariable String img_name) {
    String filePath = String.format("%s/%s/%s", username, productId, img_name);
    return productImageService.getPresignedImageURL("thedrip", filePath);
  }

  // NOTE: Get presigned URL for 1 user, 1 of their product's images
  @GetMapping("/{username}/{productId}/image")
  public List<String> getImageLink(@PathVariable String username, @PathVariable String productId) {
    return productImageService.getImagesForProduct("thedrip", username, productId).join();
  }

  // NOTE: Get presigned URL for 1 user and all of their product
  @GetMapping("/{username}/product/image")
  public List<String> getImageLink(@PathVariable String username) {
    return productImageService.getImagesForSeller("thedrip", username).join();
  }

  // NOTE: Get presigned URLs of all the products
  @GetMapping("/all/product/image")
  public List<String> getImageLink() {
    return productImageService.getAllImages("thedrip").join();
  }

  @DeleteMapping("/{username}/{productId}/{image}")
  public ResponseEntity<?> deleteImages(@PathVariable String username, @PathVariable Integer productId,
      @PathVariable String image) {
    try {
      String imgPath = username + "/" + productId + "/" + image;
      ProductImageModel imageRecord = productImageRepository.findByImgPath(imgPath).orElse(null);
      if (imageRecord == null) {
        return new ResponseEntity<>(imgPath, HttpStatus.NOT_FOUND);
      }
      productImageService.deleteImageForProduct("thedrip", username, productId, image);
      return new ResponseEntity<>("Deleted: " + imgPath, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  // NOTE: Update product details
  @PutMapping("/products")
  public ResponseEntity<List<String>> updateProducts(
      @RequestParam List<Integer> productIds,
      @RequestBody List<ProductCreationDTO> dataList) {
    try {
      List<ProductCreationDTO> productUpdateDataList = new ArrayList<>();
      for (ProductCreationDTO data : dataList) {
        productUpdateDataList.add(new ProductCreationDTO(
            data.getProductName(),
            data.getCategoryId(),
            data.getUserId(),
            data.getSeriesId(),
            data.getProductPrice(),
            data.getProductDescription(),
            data.getProductStock(),
            data.getProductSold(),
            data.getProductSizes(),
            data.getProductColors()));
      }

      List<ProductModel> updatedProducts = productService.updateProducts(productIds, productUpdateDataList);
      List<String> productNames = new ArrayList<>();
      for (ProductModel product : updatedProducts) {
        productNames.add(product.getProductName());
      }

      return new ResponseEntity<>(productNames, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/{username}/{productId}/image")
  public ResponseEntity<?> deleteImages(@PathVariable String username, @PathVariable Integer productId) {
    try {
      String imgPath = "/" + username + "/" + productId + "/";
      // ProductImageModel imageRecord =
      // productImageRepository.findByImgPath(imgPath).orElse(null);
      // if (imageRecord == null) {
      // return new ResponseEntity<>(imgPath, HttpStatus.NOT_FOUND);
      // }
      productImageService.deleteImagesForProduct("thedrip", username, productId);
      return new ResponseEntity<>("Deleted: " + imgPath, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/{username}/product/image")
  public ResponseEntity<?> deleteImages(@PathVariable String username) {
    try {
      String imgPath = "/" + username + "/";
      // ProductImageModel imageRecord =
      // productImageRepository.findByImgPath(imgPath).orElse(null);
      // if (imageRecord == null) {
      // return new ResponseEntity<>(imgPath, HttpStatus.NOT_FOUND);
      // }
      productImageService.deleteImagesForSeller("thedrip", username);
      return new ResponseEntity<>("Deleted: " + imgPath, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  // TODO: Move this to ADMIN Controller
  @DeleteMapping("/all/product/image")
  public ResponseEntity<?> deleteImages() {
    try {
      productImageService.deleteAllImages("thedrip");
      return new ResponseEntity<>("Deleted: All images of all products", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/product")
  public ResponseEntity<?> deleteProductById(@RequestParam(required = true) List<Integer> productId) {
    try {
      productService.deleteProducts(productId);
      return new ResponseEntity<>(productId, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

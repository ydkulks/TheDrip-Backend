package dev.ydkulks.TheDrip.controllers.seller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.ydkulks.TheDrip.repos.ProductImageRepository;
import dev.ydkulks.TheDrip.repos.ProductProductImageRepository;
import dev.ydkulks.TheDrip.services.ProductImageService;
import dev.ydkulks.TheDrip.models.ProductImageModel;
import dev.ydkulks.TheDrip.models.ProductProductImageModel;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@RestController
@RequestMapping("/seller")
public class SellerController {

  @Autowired
  private ProductImageRepository productImageRepository;

  @Autowired
  private ProductImageService productImageService;

  @Autowired
  private ProductProductImageRepository productProductImageRepository;

  @PostMapping("/{username}/{productId}/image")
  public ResponseEntity<String> uploadImage(@PathVariable String username, @PathVariable String productId, @RequestParam("file") List<MultipartFile> files) throws IOException {
    // Upload image to S3
    List<CompletableFuture<PutObjectResponse>> responses = productImageService.uploadMultipleFilesAsync("thedrip", username, productId, files);
    CompletableFuture.allOf(responses.toArray(new CompletableFuture[0])).join();

    // Store in product_images and product_product_images
    files.forEach(file -> {
      ProductImageModel image = new ProductImageModel();
      image.setImg_name(file.getOriginalFilename());
      image.setImg_type(file.getContentType());
      image.setImg_path(String.format("%s/%s/%s", username, productId, file.getOriginalFilename()));
      ProductImageModel savedImage = productImageRepository.save(image);

      int imgId = savedImage.getImg_id();

      ProductProductImageModel productImageLink = new ProductProductImageModel();
      productImageLink.setProduct_id(Integer.parseInt(productId));
      productImageLink.setImg_id(imgId);
      productProductImageRepository.save(productImageLink);
    });

    return ResponseEntity.ok("All files uploaded");
  }

  @GetMapping("/{username}/{productId}/{img_name}")
  public String getImageLink(@PathVariable String username, @PathVariable String productId, @PathVariable String img_name) {
    String filePath = String.format("%s/%s/%s",username,productId,img_name);
    return productImageService.getPresignedImageURL("thedrip",filePath);
  }
}

package dev.ydkulks.TheDrip.controllers.seller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.ydkulks.TheDrip.repos.ProductImageRepository;
import dev.ydkulks.TheDrip.services.ProductImageService;
import dev.ydkulks.TheDrip.models.ProductImageModel;

@RestController
@RequestMapping("/seller")
public class SellerController {

  @Autowired
  private ProductImageRepository productImageRepository;

  // @Autowired
  // private ProductImageModel productImageModel;

  @Autowired
  private ProductImageService productImageService;

  @PostMapping("/product/image")
  public ProductImageModel uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
    ProductImageModel image = new ProductImageModel();
    image.setImg_name(file.getOriginalFilename());
    image.setImg_type(file.getContentType());
    image.setImg_data(file.getBytes());
    return productImageRepository.save(image);
  }

  @GetMapping("/product/image")
  public Optional<ProductImageModel> getImage() {
    return productImageService.getImage();
  }
}

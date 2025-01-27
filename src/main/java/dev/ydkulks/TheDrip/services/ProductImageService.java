package dev.ydkulks.TheDrip.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.ProductImageModel;
import dev.ydkulks.TheDrip.repos.ProductImageRepository;


@Service
public class ProductImageService {
  @Autowired
  ProductImageRepository productImageRepository;

  public Optional<ProductImageModel> getImage() {
    return productImageRepository.findById(1);
  }

}

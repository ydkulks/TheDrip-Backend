package dev.ydkulks.TheDrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.util.List;

@SpringBootApplication
public class TheDripApplication {

  public static void main(String[] args) {
    SpringApplication.run(TheDripApplication.class, args);
    Region region = Region.AP_SOUTH_1;
    S3Client s3 = S3Client.builder()
      .region(region)
      .build();

    listBuckets(s3);
  }

  public static void listBuckets(S3Client s3) {
    try {
      ListBucketsResponse response = s3.listBuckets();
      List<Bucket> bucketList = response.buckets();
      bucketList.forEach(bucket -> {
        System.out.println("Bucket Name: " + bucket.name());
      });

    } catch (S3Exception e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      System.exit(1);
    }
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        // WARN: Change front-end URL in prod
        registry.addMapping("/**").allowedOrigins("http://localhost:5173");
      }
    };
  }

}

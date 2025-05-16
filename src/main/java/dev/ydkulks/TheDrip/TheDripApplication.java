package dev.ydkulks.TheDrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class TheDripApplication {

  public static void main(String[] args) {
    SpringApplication.run(TheDripApplication.class, args);
  }

  @Configuration
  public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
      // WARN: Change front-end URL in prod
      registry
        .addMapping("/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET", "POST","PUT","DELETE","OPTIONS", "PATCH")
        .allowedHeaders("Authorization", "Content-Type") // Allow Authorization header
        .allowCredentials(true);
    }
  };

}

package dev.ydkulks.TheDrip.services;

// import java.security.Key;
// import java.security.NoSuchAlgorithmException;
// import java.util.Base64;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;

// import javax.crypto.KeyGenerator;
// import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

  // private String secretKey = "";

  // public JWTService() {
  //   try {
  //     KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
  //     SecretKey sk = keyGen.generateKey();
  //     secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
  //   } catch (NoSuchAlgorithmException e) {
  //     throw new RuntimeException(e);
  //   }
  // }

  public String generateToken(String username) {

    // Map<String, Object> claims = new HashMap<>();
    // return Jwts.builder()
    //   .claims()
    //   .add(claims)
    //   .subject(username)
    //   .issuedAt(new Date(System.currentTimeMillis()))
    //   .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
    //   .and()
    //   .compact()
    //   .signWith(getKey());

    return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFkbWluQGdtYWlsLmNvbSIsInVzZXJuYW1lIjoiYWRtaW4iLCJwYXNzd29yZCI6ImFkbWluQDEyMyIsInJvbGUiOiJBZG1pbiIsImlhdCI6MTcxNjIzOTAyMiwiZXhwIjoxNzQ2MjM5MDIyfQ.vTdX9LzrNrdbf0xwbXwpcHcFLwNEou5fPUXhHVO0boQ";
  }

  // private Key getKey() {
  //   byte[] keyBytes = Decoders.BASE64.decode(secretKey);
  //   return Keys.hmacShaKeyFor(secretKey);
  // }
}

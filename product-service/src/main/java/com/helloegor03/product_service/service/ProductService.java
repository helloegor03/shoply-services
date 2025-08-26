package com.helloegor03.product_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.helloegor03.product_service.config.JwtUtil;
import com.helloegor03.product_service.product.Product;
import com.helloegor03.product_service.repository.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;
    private final JwtUtil jwtUtil;


    public ProductService(ProductRepository productRepository, Cloudinary cloudinary, JwtUtil jwtUtil) {
        this.productRepository = productRepository;
        this.cloudinary = cloudinary;
        this.jwtUtil = jwtUtil;

    }

    public Optional<Product> getCurrentUserProfile(Authentication authentication){
        String username = authentication.getName();

        return productRepository.findByUsername(username);
    }

    public Product createProduct(Product product, String token, MultipartFile imageFile) throws IOException {
        String username = jwtUtil.getUsernameFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);

        product.setUsername(username);
        product.setUserId(userId);

        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("secure_url").toString();
            product.setImageUrl(imageUrl);
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id){
        if(productRepository.findById(id).isEmpty()){
            throw new RuntimeException("Not found this advertisement");
        }
        productRepository.deleteById(id);
    }



}

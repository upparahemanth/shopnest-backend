package com.shopnest.service;

import com.shopnest.dto.request.ProductRequest;
import com.shopnest.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    ProductResponse getProductById(Long id);
    Page<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction);
    List<ProductResponse> getProductsByCategory(Long categoryId);
    Page<ProductResponse> searchProducts(String keyword, int page, int size);
}
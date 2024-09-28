package org.example.cafekiosk.spring.api.controller.product;

import lombok.RequiredArgsConstructor;
import org.example.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import org.example.cafekiosk.spring.api.serveice.product.ProductService;
import org.example.cafekiosk.spring.api.serveice.product.response.ProductResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class ProductController {

    final ProductService _productService;

    @PostMapping("/api/v1/products/new")
    public ProductResponse createProduct(@RequestBody ProductCreateRequest request) {
        return _productService.createProduct(request);
    }

    @GetMapping("/api/v1/products/selling")
    public List<ProductResponse> getSellingProducts() {
        return _productService.getSellingProducts();
    }

}

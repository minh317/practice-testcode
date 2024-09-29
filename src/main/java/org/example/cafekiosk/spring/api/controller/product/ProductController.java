package org.example.cafekiosk.spring.api.controller.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cafekiosk.spring.api.ApiResponse;
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
    public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreateRequest request) {
        return ApiResponse.ok(_productService.createProduct(request));
    }

    @GetMapping("/api/v1/products/selling")
    public ApiResponse<List<ProductResponse>> getSellingProducts() {
        return ApiResponse.ok(_productService.getSellingProducts());
    }

}

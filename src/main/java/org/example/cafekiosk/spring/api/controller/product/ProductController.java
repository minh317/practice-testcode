package org.example.cafekiosk.spring.api.controller.product;

import lombok.RequiredArgsConstructor;
import org.example.cafekiosk.spring.api.serveice.product.ProductService;
import org.example.cafekiosk.spring.api.serveice.product.response.ProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class ProductController {

    final ProductService _productService;

    @GetMapping("/api/v1/products/selling")
    public List<ProductResponse> getSellingProducts() {
        return _productService.getSellingProducts();
    }

}

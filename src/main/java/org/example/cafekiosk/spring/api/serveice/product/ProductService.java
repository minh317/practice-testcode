package org.example.cafekiosk.spring.api.serveice.product;

import lombok.RequiredArgsConstructor;
import org.example.cafekiosk.spring.api.serveice.product.response.ProductResponse;
import org.example.cafekiosk.spring.domain.product.Product;
import org.example.cafekiosk.spring.domain.product.ProductRepository;
import org.example.cafekiosk.spring.domain.product.ProductSellingStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    final ProductRepository _productRepository;

    public List<ProductResponse> getSellingProducts() {

        List<Product> products =
                _productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }
}

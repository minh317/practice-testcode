package org.example.cafekiosk.spring.api.serveice.product;

import lombok.RequiredArgsConstructor;
import org.example.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import org.example.cafekiosk.spring.api.serveice.product.response.ProductResponse;
import org.example.cafekiosk.spring.domain.product.Product;
import org.example.cafekiosk.spring.domain.product.ProductRepository;
import org.example.cafekiosk.spring.domain.product.ProductSellingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CQRS - Command / Query Responsibility Segregation (명령과 조회의 책임을 분리)
 *      -> Command와 Query의 비율은 약 2:8 수준으로 차이가 많이 나므로 Master/Slave DB에 (ex. mysql or auraDB) readonly 설정을 기반으로 나눌 수 있다.

 * 추천 : Class 상단에 @Transactional(readonly = true)를 설정하고, Command성의 메서드에다가 @Transactional을 설정한다.
 */
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

    final ProductRepository _productRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        String nextProductNumber = createNextProductNumber();

        Product product = request.toServiceRequest().toEntity(nextProductNumber);
        Product savedProduct = _productRepository.save(product);

        return ProductResponse.of(savedProduct);
    }

    public List<ProductResponse> getSellingProducts() {

        List<Product> products =
                _productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    private String createNextProductNumber() {
        String latestProductNumber  = _productRepository.findLatestProduct();
        if (latestProductNumber == null) {
            return "001";
        }

        int latestProductNumberInt = Integer.parseInt(latestProductNumber);
        int nextProductNumber = latestProductNumberInt + 1;

        return String.format("%03d", nextProductNumber);
    }
}

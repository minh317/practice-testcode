package org.example.cafekiosk.spring.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllBySellingStatusIn(List<ProductSellingStatus> sellingStatuses);

    @Query(value = "SELECT p.product_number FROM product p ORDER BY p.id DESC LIMIT 1", nativeQuery = true)
    String findLatestProduct();
}
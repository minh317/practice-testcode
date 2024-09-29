package org.example.cafekiosk.spring.api.serveice.order;

import lombok.RequiredArgsConstructor;
import org.example.cafekiosk.spring.api.serveice.order.request.OrderCreateServiceRequest;
import org.example.cafekiosk.spring.api.serveice.order.response.OrderResponse;
import org.example.cafekiosk.spring.domain.order.Order;
import org.example.cafekiosk.spring.domain.order.OrderRepository;
import org.example.cafekiosk.spring.domain.product.Product;
import org.example.cafekiosk.spring.domain.product.ProductRepository;
import org.example.cafekiosk.spring.domain.product.ProductType;
import org.example.cafekiosk.spring.domain.stock.Stock;
import org.example.cafekiosk.spring.domain.stock.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    final ProductRepository _productRepository;
    final OrderRepository _orderRepository;
    final StockRepository _stockRepository;

    /**
     * 재고 감소 -> 동시성 문제의 대표적인 문제
     * optimistic lock / pessimistic lock
     *
     * @param request
     * @param registeredDateTime
     * @return
     */
    @Transactional
    public OrderResponse createOrder(OrderCreateServiceRequest request, LocalDateTime registeredDateTime) {

        List<String> productNumbers = request.getProductNumbers();
        List<Product> products = findProductsBy(productNumbers);

        deductStockQuantities(products);

        Order order = Order.create(products, registeredDateTime);
        Order savedOrder = _orderRepository.save(order);

        return OrderResponse.of(savedOrder);
    }

    private void deductStockQuantities(List<Product> products) {
        List<String> stockProductNumbers = extractStockProductNumbers(products);

        Map<String, Stock> stockMap = createStockMap(stockProductNumbers);
        Map<String, Long> productCountingMap = createCountingMap(stockProductNumbers);

        for(String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();

            if (stock.isQuantityLessThan(quantity)) {
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
            }

            /**
             * 해당 메서드 안에서 한번 더 재고를 체크하는 이유는 관점을 다르게 봐야 한다.
             * 서비스에서 지나간 체크는 주문 생성 로직을 수행하다가 stock에 대한 차감을 시도하는 과정인 것이고,
             *
             * deductQuantity() 메서드 내부에서의 검증 자체는 외부의 서비스 로직을 모르기도 하고,
             * 메서드 자체의 역할을 보장해줘야 하기 때문에 검증을 하는 것이다. (다른 곳에서도 쓰일 수 있고)
             */

            stock.deductQuantity(quantity);
        }
    }

    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = _productRepository.findAllByProductNumberIn(productNumbers);

        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNumber, product -> product));

        return productNumbers.stream()
                .map(productMap::get)
                .collect(Collectors.toList());
    }

    private List<String> extractStockProductNumbers(List<Product> products) {
        return products.stream()
                .filter(product -> ProductType.containsStockType(product.getType()))
                .map(Product::getProductNumber)
                .collect(Collectors.toList());
    }

    private Map<String, Stock> createStockMap(List<String> stockProductNumbers) {
        List<Stock> stocks = _stockRepository.findAllByProductNumberIn(stockProductNumbers);
        return stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, stock -> stock));
    }

    private static Map<String, Long> createCountingMap(List<String> stockProductNumbers) {
        return stockProductNumbers.stream()
                .collect(Collectors.groupingBy(productNumber -> productNumber, Collectors.counting()));
    }
}

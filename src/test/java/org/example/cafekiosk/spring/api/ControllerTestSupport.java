package org.example.cafekiosk.spring.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cafekiosk.spring.api.controller.order.OrderController;
import org.example.cafekiosk.spring.api.controller.product.ProductController;
import org.example.cafekiosk.spring.api.serveice.order.OrderService;
import org.example.cafekiosk.spring.api.serveice.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        OrderController.class,
        ProductController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc _mockMvc;

    @Autowired
    protected ObjectMapper _objectMapper;

    @MockBean
    protected OrderService _orderService;

    @MockBean
    protected ProductService _productService;
}

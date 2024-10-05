package org.example.cafekiosk.spring.api.controller.order;

import org.example.cafekiosk.spring.api.ControllerTestSupport;
import org.example.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import org.example.cafekiosk.spring.api.serveice.order.response.OrderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class OrderControllerTest extends ControllerTestSupport {

    @DisplayName("신규 주문을 등록한다.")
    @Test
    void createOrder() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001"))
                .build();

        OrderResponse result = OrderResponse.builder().build();
        when(_orderService.createOrder(request.toServiceRequest(), LocalDateTime.now())).thenReturn(result);

        // when & then
        _mockMvc.perform(  // perform : API를 수행한다.
                    post("/api/v1/orders/new")
                        .content(_objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("신규 주문을 생성할 때 반드시 상품 번호는 1개 이상이여야 한다.")
    @Test
    void createOrderWithoutEmptyProductNumbers() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(Collections.emptyList())
                .build();

        OrderResponse result = OrderResponse.builder().build();
        when(_orderService.createOrder(request.toServiceRequest(), LocalDateTime.now())).thenReturn(result);

        // when & then
        _mockMvc.perform(  // perform : API를 수행한다.
                    post("/api/v1/orders/new")
                        .content(_objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상품 번호 리스트는 필수입니다."));
    }
}
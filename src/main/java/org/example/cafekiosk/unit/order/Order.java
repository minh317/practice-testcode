package org.example.cafekiosk.unit.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.cafekiosk.unit.beverages.Beverage;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@RequiredArgsConstructor
public class Order {

    private final LocalDateTime orderDateTime;
    private final List<Beverage> beverages;


}

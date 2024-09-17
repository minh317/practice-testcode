package org.example.cafekiosk.unit.beverages;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AmericanoTest {

    public static final String AMERICANO = "아메리카노";

    @Test
    void getName() {
        Americano americano = new Americano();

        assertEquals(AMERICANO, americano.getName());
        assertThat(americano.getName()).isEqualTo(AMERICANO);
    }

    @Test
    void getPrice() {
        Americano americano = new Americano();

        assertThat(americano.getPrice()).isEqualTo(4000);
    }
}
package com.costa.luiz.test.producer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HotelPojoTest {

    @Test
    void create() {
        Integer id = 23;
        String name = "Jordan";
        Hotel hotel = new Hotel(id, name);
        assertThat(hotel)
                .extracting("id", "name")
                .contains(id, name);
    }
}

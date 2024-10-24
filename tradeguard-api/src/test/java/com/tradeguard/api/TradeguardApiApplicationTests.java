package com.tradeguard.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TradeguardApiApplicationTests {

    @DynamicPropertySource
    @Description("Sets the server.port value to dynamically use a random port.")
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> 0);
    }

    @Test
    @Description("Verifies that the Spring context loads successfully.")
    void contextLoads() {
    }

    @Test
    @Description("Verifies that the application can start successfully by running the main method of TradeguardApiApplication.")
    void testMain() {
        TradeguardApiApplication.main(new String[] {});
    }
}

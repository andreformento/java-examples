package com.formento.web;

import com.formento.web.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

@SpringBootTest
@ImportTestcontainers(TestContainersConfig.class)
class WebApplicationTests {

	@Test
	void contextLoads() {
	}

}

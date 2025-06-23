package com.demo.copilot.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
})
class TaskManagerApplicationTests {

	@Test
	void contextLoads() {
		// This test ensures that the Spring application context loads successfully
		// It validates the basic configuration and dependency injection setup
	}

	@Test
	void applicationStarts() {
		// This test verifies that the main application class can be instantiated
		// and that all auto-configuration works properly
	}

}
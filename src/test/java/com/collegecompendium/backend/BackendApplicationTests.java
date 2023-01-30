package com.collegecompendium.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Spring annotation - flags this as a test suite
@SpringBootTest
// Spring annotation - uses the given profile for tests
@ActiveProfiles("dev")
class BackendApplicationTests {
	@Test
	void contextLoads() {
	}
}

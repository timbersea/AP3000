package com.an;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

import static org.slf4j.LoggerFactory.getLogger;

@SpringBootTest
public class AP3000ApplicationTests {
    private static final Logger logger = getLogger(AP3000ApplicationTests.class);
    @Resource
    Environment environment;

	@Test
	public void contextLoads() {
        logger.info(environment.toString());
	}

}

package com.an;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

    @Test
    public void test0(){
        for (int i = 0; i < 100; i++) {
            String format = String.format("%016d", Long.parseLong("1726839741913346048"));
            log.info("test0:{}",format);
        }
    }
}

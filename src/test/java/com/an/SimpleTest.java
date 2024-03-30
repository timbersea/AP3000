package com.an;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class SimpleTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

    @Test
    public void test0(){
        for (int i = 0; i < 100; i++) {
            String format = String.format("%016d", Long.parseLong("1726839741913346048"));
            log.info("test0:{}",format);
        }
    }
    public void test1(){
        byte[] bytes = "134.175.12.227".getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = new byte[46];
        System.arraycopy(bytes,0,bytes1,0,bytes.length);
        log.info("test1:{}", DatatypeConverter.printHexBinary(bytes1));
    }
}

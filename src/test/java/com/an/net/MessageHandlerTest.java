package com.an.net;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class MessageHandlerTest {
    @Test
    public void test1() {
        UDianPackage msg = UDianPackage.buildFromHexString(
                "444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05");
        log.info("{}", msg);
    }
}
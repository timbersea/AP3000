package com.an.net;

import junit.framework.TestCase;

public class MessageHandlerTest extends TestCase {

    public void testSummationCheak() {
        MessageUtil.summationCheak(
                "444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05");
    }
    public void test1(){
        UDianPackage msg = UDianPackage.buildFromHexString("444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05");
    }
}
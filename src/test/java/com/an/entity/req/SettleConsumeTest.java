package com.an.entity.req;

import com.an.common.ResponseCode;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettleConsumeTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(SettleConsumeTest.class);

    public void testGetStopReasonDescription() {
        for (int i = 0; i < 32; i++) {
            log.info("testGetStopReasonDescription: {}", ResponseCode.getStopReasonDescription((byte) i) );
        }
    }
}
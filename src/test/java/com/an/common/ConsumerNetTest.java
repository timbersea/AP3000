package com.an.common;

import com.an.AP3000ApplicationTests;
import com.anju.common.dto.OrderAutoFinishChargeDto;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ConsumerNetTest extends AP3000ApplicationTests {
    @Resource
    ConsumerNet consumerNet;

    public void testNotifyStartResult() {
    }

    @Test
    public void testFinishOrder() {
        consumerNet.finishOrder(new OrderAutoFinishChargeDto());
    }

    public void testUpdatePileStatus() {
    }
}
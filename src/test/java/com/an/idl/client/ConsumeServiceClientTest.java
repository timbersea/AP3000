package com.an.idl.client;

import com.an.AP3000ApplicationTests;
import com.an.idl.consumer.HeatBeat;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class ConsumeServiceClientTest extends AP3000ApplicationTests {
    @Resource
    ConsumeServiceClient client;

    @Test
    public void testHeatBeat() {
        HeatBeat heatBeat = new HeatBeat();
        heatBeat.setFirmwareVersion((short) 99);
        client.heatBeat(123456,heatBeat);
    }

    public void testRegister() {
    }

    public void testHeatBeat21() {
    }

    public void testSwipingChard() {
    }

    public void testPortChargePowerHeatBeat() {
    }

    public void testCabinetHeatBeat() {
    }

    public void testWarnPush() {
    }

    public void testChargeFinish() {
    }

    public void testPortStatusPush() {
    }

    public void testSettleConsume() {
    }
}
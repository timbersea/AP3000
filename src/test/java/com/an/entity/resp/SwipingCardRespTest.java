package com.an.entity.resp;

import junit.framework.TestCase;

public class SwipingCardRespTest extends TestCase {

    public void testData() {
        SwipingCardResp swipingCardResp = new SwipingCardResp();
        swipingCardResp.setCardId(1);
        swipingCardResp.setPort((byte) 2);
        swipingCardResp.setStatus((byte) 3);
        swipingCardResp.setFeeType((byte) 4);
        swipingCardResp.setBalanceValidateDate(5);
        byte[] data = swipingCardResp.data();
    }
}
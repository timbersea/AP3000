package com.an.service;


import com.an.common.ConsumerNet;
import com.an.common.ResponseCode;
import com.an.idl.server.AP3000ServiceImpl;
import com.an.idl.ap3000.StartCharge;
import com.an.idl.ap3000.StartChargeResp;
import com.anju.common.core.domain.AjaxResult;
import com.anju.common.dto.OrderAutoFinishChargeDto;
import com.anju.common.dto.iot.StartChargeRequestDto;
import com.anju.common.dto.iot.StopChargeRequestDto;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AP3000Service {
    private static final Logger log = LoggerFactory.getLogger(AP3000Service.class);
    @Resource
    AP3000ServiceImpl ap3000Service;
    @Resource
    ConsumerNet consumerNet;

    public AjaxResult start(StartChargeRequestDto dto) throws TException {
        StartCharge startCharge = new StartCharge();
        startCharge.setFeeType((byte) 0);
        startCharge.setBalanceValidateDate(dto.getBalance());
        startCharge.setPort(Byte.parseByte(dto.getGunCode()));
        startCharge.setChargeCommand((byte) 1);
        startCharge.setChargeTimeElectric((short) 0);
        startCharge.setOrderNo(Long.parseUnsignedLong(dto.getOrderNo()));
        startCharge.setMaxChargePower((short) 0);
        startCharge.setMaxChargeTime((short) 0);
        startCharge.setQRCodeLight((byte) 1);
        startCharge.setLongChargeMode((byte) 1);
        startCharge.setExtraChargeTime((short) 0xFFFF);
        startCharge.setSkipShortCircuitCheck((byte) 2);

        StartChargeResp startChargeResp = ap3000Service.startChargeCommand(Integer.parseInt(dto.getPileCode()),
                startCharge);
        if(startChargeResp.getResp()==0){
            return AjaxResult.success();
        }
        return AjaxResult.error("启用充电失败+reason code:"+ ResponseCode.getStartChargeResponse(startChargeResp.getResp()));
    }


    public AjaxResult stop(StopChargeRequestDto dto) throws TException {
        StartCharge startCharge = new StartCharge();
        startCharge.setFeeType((byte) 0);
        //startCharge.setBalanceValidateDate();
        startCharge.setPort((byte)Byte.parseByte(dto.getGunCode()));
        startCharge.setChargeCommand((byte) 0);
        startCharge.setChargeTimeElectric((short) 0);
        startCharge.setOrderNo(1111111111111111111L);
        startCharge.setMaxChargePower((short) 0);
        startCharge.setMaxChargeTime((short) 0);
        startCharge.setQRCodeLight((byte) 1);
        startCharge.setLongChargeMode((byte) 1);
        startCharge.setExtraChargeTime((short) 0xFFFF);
        startCharge.setSkipShortCircuitCheck((byte) 2);

        StartChargeResp startChargeResp = ap3000Service.startChargeCommand(Integer.parseInt(dto.getPileCode()),
                startCharge);
        if(startChargeResp.getResp()==0){
            OrderAutoFinishChargeDto orderAutoFinishChargeDto = new OrderAutoFinishChargeDto();
            return AjaxResult.success();
        }
        return AjaxResult.error("结束充电失败+reason："+ResponseCode.getStartChargeResponse(startChargeResp.getResp()));

    }

    public  void restart(String pileCode){
        try {
            ap3000Service.resetAndRestart(Integer.parseInt(pileCode));
        } catch (TException e) {
            log.error(e.getMessage(), e);
        }
    }
}

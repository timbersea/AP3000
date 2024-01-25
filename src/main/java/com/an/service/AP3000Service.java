package com.an.service;


import com.an.idl.AP3000ServiceImpl;
import com.an.idl.StartCharge;
import com.an.idl.StartChargeResp;
import com.anju.common.core.domain.AjaxResult;
import com.anju.common.dto.iot.StartChargeRequestDto;
import com.anju.common.dto.iot.StopChargeRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j(topic = "Ykc16Service")
@Service
public class AP3000Service {
    @Resource
    AP3000ServiceImpl ap3000Service;

    public AjaxResult start(StartChargeRequestDto dto) throws TException {
        StartCharge startCharge = new StartCharge();
        startCharge.setFeeType((byte) 0);
        startCharge.setBalanceValidateDate(dto.getBalance());
        startCharge.setPort((byte)mapPort(dto.getGunCode()));
        startCharge.setChargeCommand((byte) 1);
        startCharge.setChargeTimeElectric((short) 0);
        startCharge.setOrderNo(dto.getOrderNo());
        startCharge.setMaxChargePower((short) 0);
        startCharge.setMaxChargeTime((short) 0);
        startCharge.setQRCodeLight((byte) 1);
        startCharge.setLongChargeMode((byte) 1);
        startCharge.setExtraChargeTime((short) 0xFFFF);
        startCharge.setSkipShortCircuitCheck((byte) 2);

        StartChargeResp startChargeResp = ap3000Service.startChargeCommand(mapPhysicalId(dto.getPileCode()), startCharge);
        if(startChargeResp.getResp()==0){
            return AjaxResult.success();
        }
        return AjaxResult.error("启用充电失败+reason code:"+startChargeResp.getResp());
    }
    private int mapPhysicalId(String pipeCode){
        return 0;
    }

    private int mapPort(String pipeCode){
        return 0;
    }


    public AjaxResult stop(StopChargeRequestDto dto) throws TException {
        StartCharge startCharge = new StartCharge();
        startCharge.setFeeType((byte) 0);
    //    startCharge.setBalanceValidateDate(dto.getBalance());
        startCharge.setPort((byte)mapPort(dto.getGunCode()));
        startCharge.setChargeCommand((byte) 0);
        startCharge.setChargeTimeElectric((short) 0);
    //    startCharge.setOrderNo(dto.getOrderNo());
        startCharge.setMaxChargePower((short) 0);
        startCharge.setMaxChargeTime((short) 0);
        startCharge.setQRCodeLight((byte) 1);
        startCharge.setLongChargeMode((byte) 1);
        startCharge.setExtraChargeTime((short) 0xFFFF);
        startCharge.setSkipShortCircuitCheck((byte) 2);

        StartChargeResp startChargeResp = ap3000Service.startChargeCommand(mapPhysicalId(dto.getPileCode()), startCharge);
        if(startChargeResp.getResp()==0){
            return AjaxResult.success();
        }
        return AjaxResult.error("启用充电失败+reason code:"+startChargeResp.getResp());

    }
}

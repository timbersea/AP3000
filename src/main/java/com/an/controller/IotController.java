package com.an.controller;

import com.an.service.IotServiceImpl;
import com.anju.common.core.domain.AjaxResult;
import com.anju.common.dto.iot.RestartRequestDto;
import com.anju.common.dto.iot.StartChargeRequestDto;
import com.anju.common.dto.iot.StopChargeRequestDto;
import org.apache.thrift.TException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class IotController {
    @Resource
    IotServiceImpl iotService;


    /**
     * 远程升级
     * @author LeiFengLiang
     * @createTime  2024/1/4 11:26
     * @Param [pileCode]
     * @return com.anju.common.core.domain.AjaxResult
     **/
//    @PostMapping("/upgrade")
//    public AjaxResult upgrade(@RequestBody UpgradeRequest dto) {
//        return iotService.upgrade(dto);
//    }


    /**
     * 同步计费规则
     */
    @PostMapping("/syncFeeRule")
    public AjaxResult syncFeeRule(Long stationId, String pileCode) {
        iotService.syncFeeRule(stationId, pileCode);
        return AjaxResult.success();
    }

    /**
     * 重启
     */
    @PostMapping("/restart")
    public AjaxResult restart(@RequestBody RestartRequestDto dto) {
        iotService.restart(dto);
        return AjaxResult.success();
    }

    /**
     * 开始充电
     */
    @PostMapping("/startCharge")
    public AjaxResult startCharge(@RequestBody StartChargeRequestDto dto) throws TException {
        return iotService.startCharge(dto);
    }

    /**
     * 停止充电
     */
    @PostMapping("/stopCharge")
    public AjaxResult stopCharge(@RequestBody StopChargeRequestDto dto) throws TException {
        return iotService.stopCharge(dto);
    }
}

package com.an.service;

import com.anju.common.core.domain.AjaxResult;
import com.anju.common.domain.FeeRule;
import com.anju.common.dto.iot.RestartRequestDto;
import com.anju.common.dto.iot.StartChargeRequestDto;
import com.anju.common.dto.iot.StopChargeRequestDto;
import com.anju.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class IotServiceImpl {
    @Resource
    AP3000Service ap3000Service;
//    @Resource
//    YkcManage ykcManage;
//    @Resource
//    RedisCache redisCache;
//    @Resource
//    Ykc18Service ykc18Service;


    /**
     * 远程升级
     * @author LeiFengLiang
     * @createTime  2024/1/4 11:24
     * @Param [pileCode]
     * @return com.anju.common.core.domain.AjaxResult
     **/
//    public  AjaxResult upgrade(UpgradeRequest dto) {
//        return ykc18Service.upgrade(dto);
//
//    }

    public AjaxResult startCharge(StartChargeRequestDto dto) throws TException {
        if (StringUtils.isBlank(dto.getLogicNo())) {
            dto.setLogicNo("1");
        }
        return ap3000Service.start(dto);
    }

    public AjaxResult stopCharge(StopChargeRequestDto dto) throws TException {
        return ap3000Service.stop(dto);
    }

    /**
     * 重启
     */
    public void restart(RestartRequestDto dto) {
        if (StringUtils.isNotEmpty(dto.getPileCodeList())) {
            for (String pileCode : dto.getPileCodeList()) {
                ap3000Service.restart(pileCode);
            }
        }
    }

    /**
     * 同步计费规则-站点所有桩
     *
     * @param stationId 站点id
     */
    public void syncFeeRule(Long stationId, String pileCode) {
        // 同步计费规则
//        FeeRuleApplyResponse response = getFeeRuleProtocol(pileCode);
//        response.setPileCode(pileCode);
//        response.setModeCode("0100");
//        YkcProtocol<FeeRuleApplyResponse> protocol = new YkcProtocol<>();
//        protocol.setSeq("0000");
//        protocol.setType("58");
//        protocol.setDataBody(response);
//        String cmd = protocol.buildCmd();
//        ykcManage.send(pileCode, cmd);
    }

    /**
     * 生成计费规则请求
     */

    /**
     * 获取时间段所属的时段
     */
    public String getZoneFeeRule(List<FeeRule> feeRuleList) {
        feeRuleList.sort(Comparator.comparing(FeeRule::getStartTime));
        FeeRule feeRule = feeRuleList.get(feeRuleList.size() - 1);
        feeRule.setEndTime(feeRule.getEndTime().plusDays(1));
        StringBuilder cmd = new StringBuilder();
        for (FeeRule obj : feeRuleList) {
            LocalDateTime startTime = obj.getStartTime();
            LocalDateTime endTime = obj.getEndTime();
            while (startTime.isBefore(endTime)) {
                if (1 == obj.getStageType()) {
                    cmd.append("00");
                } else if (2 == obj.getStageType()) {
                    cmd.append("01");
                } else if (3 == obj.getStageType()) {
                    cmd.append("02");
                } else if (4 == obj.getStageType()) {
                    cmd.append("03");
                }
                // 更新时间，增加30分钟间隔
                startTime = startTime.plusMinutes(30);
            }
        }
        return cmd.toString();
    }

}

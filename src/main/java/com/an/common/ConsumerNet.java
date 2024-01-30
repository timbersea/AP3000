package com.an.common;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.anju.common.core.domain.AjaxResult;
import com.anju.common.dto.OrderAutoFinishChargeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConsumerNet {
    private static final Logger log = LoggerFactory.getLogger(ConsumerNet.class);
    @Value("${consumerUrl}")
    private String consumerUrl;

    public void notifyStartResult(String orderNo, Boolean result, String responseValue) {
        String url = UrlBuilder.of(consumerUrl + "/consumerApi/order/orderRecord/startChargeNotify")
                .addQuery("orderNo", orderNo)
                .addQuery("result", result)
                .addQuery("failMsg", responseValue)
                .build();
        for (int i = 0; i < 5; i++) {
            try (HttpResponse httpResponse = HttpRequest.post(url).timeout(2000).execute()) {
                String body = httpResponse.body();
                AjaxResult ajaxResult = JSON.parseObject(body, AjaxResult.class);
                if (ajaxResult.isSuccess()) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void finishOrder(OrderAutoFinishChargeDto dto) {
        log.info("finishOrder:dto = [{}]", dto);
        String params = JSON.toJSONString(dto);
        // 通知消费端，订单已结束
        String url = UrlBuilder.of(consumerUrl + "/consumerApi/order/orderRecord/autoFinish").build();
        for (int i = 0; i < 5; i++) {
            try (HttpResponse httpResponse = HttpRequest.post(url).body(params).timeout(2000).execute()) {
                String body = httpResponse.body();
                AjaxResult ajaxResult = JSON.parseObject(body, AjaxResult.class);
                if (ajaxResult.isSuccess()) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void updatePileStatus(String pileCode, String gunCode, Integer pileStatus, Integer gunStatus) {
        // 通知消费端，订单已结束
        String url = UrlBuilder.of(consumerUrl + "/consumerApi/order/orderRecord/updatePileStatus")
                .addQuery("pileCode", pileCode)
                .addQuery("gunCode", gunCode)
                .addQuery("pileStatus", pileStatus)
                .addQuery("gunStatus", gunStatus)
                .build();
        try (HttpResponse httpResponse = HttpRequest.post(url).execute()) {
            String body = httpResponse.body();
        }
    }
}

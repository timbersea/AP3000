package com.an.controller;

import com.an.dto.FirmwareUpdate;
import com.an.dto.FirmwareUpdateF8;
import com.an.dto.FirmwareUpdateResp;
import com.an.dto.MaxTimePower;
import com.an.dto.ModifyChargeParam;
import com.an.dto.MutiFunction;
import com.an.dto.PowerSettings1;
import com.an.dto.PowerSettings2;
import com.an.dto.QRCode;
import com.an.dto.ReadEEPROM;
import com.an.dto.ReadEEPROMResp;
import com.an.dto.Reserve;
import com.an.dto.StartCharge;
import com.an.dto.StartChargeResp;
import com.an.dto.StopCharge;
import com.an.dto.StopChargeResp;
import com.an.dto.UserCard;
import com.an.dto.Voice;
import com.an.dto.WriteEEPROM;
import com.an.service.AP3000Service;
import io.netty.buffer.ByteBuf;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

/**
 * 提供http接口供其它系统调用，无鉴权，仅供内部网络调用。
 * 阻塞设备 IO 的接口返回 {@link Callable}，由独立线程池执行，避免占满 Tomcat 工作线程。
 */
@RestController
public class AP3000Controller {
    @Resource
    AP3000Service ap3000Service;


    @PostMapping("/send")
    public Callable<ByteBuf> send(int pileCode, byte command, ByteBuffer data) {
        return () -> ap3000Service.send(pileCode, command, data);
    }


    @PostMapping("/syncDeviceStatus")
    public void syncDeviceStatus(int pileCode, byte command) {
        ap3000Service.syncDeviceStatus(pileCode, command);
    }


    @PostMapping("/startChargeCommand")
    public Callable<StartChargeResp> startChargeCommand(@RequestBody StartCharge p) {
        return () -> ap3000Service.startChargeCommand(p);
    }


    @PostMapping("/modifyChargePara")
    public Callable<Byte> modifyChargePara(@RequestBody ModifyChargeParam p) {
        return () -> ap3000Service.modifyChargePara(p);
    }


    @PostMapping("/powerSettings1")
    public Callable<Byte> powerSettings1(@RequestBody PowerSettings1 p) {
        return () -> ap3000Service.powerSettings1(p);
    }


    @PostMapping("/powerSettings2")
    public Callable<Byte> powerSettings2(@RequestBody PowerSettings2 p) {
        return () -> ap3000Service.powerSettings2(p);
    }


    @PostMapping("/setMaxChargeTimePower")
    public Callable<Byte> setMaxChargeTimePower(@RequestBody MaxTimePower p) {
        return () -> ap3000Service.setMaxChargeTimePower(p);
    }


    @PostMapping("/setUserCard")
    public Callable<Byte> setUserCard(@RequestBody UserCard p) {
        return () -> ap3000Service.setUserCard(p);
    }


    @PostMapping("/resetAndRestart")
    public Callable<Byte> resetAndRestart(int pileCode) {
        return () -> ap3000Service.resetAndRestart(pileCode);
    }


    @PostMapping("/romClean")
    public Callable<Byte> romClean(int pileCode) {
        return () -> ap3000Service.romClean(pileCode);
    }


    @PostMapping("/playVoice")
    public Callable<Byte> playVoice(@RequestBody Voice p) {
        return () -> ap3000Service.playVoice(p);
    }


    @PostMapping("/setDeviceWorkMode")
    public Callable<Byte> setDeviceWorkMode(int pileCode, byte p) {
        return () -> ap3000Service.setDeviceWorkMode(pileCode, p);
    }


    @PostMapping("/deviceUpdatePackage")
    public Callable<FirmwareUpdateResp> deviceUpdatePackage(@RequestBody FirmwareUpdate p) {
        return () -> ap3000Service.deviceUpdatePackage(p);
    }


    @PostMapping("/deviceUpdatePackageF8")
    public Callable<FirmwareUpdateResp> deviceUpdatePackageF8(@RequestBody FirmwareUpdateF8 p) {
        return () -> ap3000Service.deviceUpdatePackageF8(p);
    }


    @PostMapping("/query90")
    public Callable<PowerSettings1> query90(int pileCode) {
        return () -> ap3000Service.query90(pileCode);
    }


    @PostMapping("/query91")
    public Callable<PowerSettings2> query91(int pileCode) {
        return () -> ap3000Service.query91(pileCode);
    }


    @PostMapping("/query92")
    public Callable<PowerSettings2> query92(int pileCode) {
        return () -> ap3000Service.query92(pileCode);
    }


    @PostMapping("/query93")
    public Callable<UserCard> query93(int pileCode) {
        return () -> ap3000Service.query93(pileCode);
    }


    @Deprecated
    @PostMapping("/query94")
    public MaxTimePower query94(int pileCode) {
        return ap3000Service.query94(pileCode);
    }


    @PostMapping("/readEEPROM")
    public Callable<ReadEEPROMResp> readEEPROM(@RequestBody ReadEEPROM p) {
        return () -> ap3000Service.readEEPROM(p);
    }


    @PostMapping("/writeEEPROM")
    public Callable<Byte> writeEEPROM(@RequestBody WriteEEPROM p) {
        return () -> ap3000Service.writeEEPROM(p);
    }


    @PostMapping("/modifyQRCode")
    public Callable<Byte> modifyQRCode(@RequestBody QRCode p) {
        return () -> ap3000Service.modifyQRCode(p);
    }


    @PostMapping("/setTCMode")
    public Callable<Byte> setTCMode(int pileCode, byte mode) {
        return () -> ap3000Service.setTCMode(pileCode, mode);
    }


    @PostMapping("/stopCharge")
    public Callable<StopChargeResp> stopCharge(@RequestBody StopCharge p) {
        return () -> ap3000Service.stopCharge(p);
    }


    @PostMapping("/temporaryQRCode")
    public Callable<Byte> temporaryQRCode(int pileCode, ByteBuffer p) {
        return () -> ap3000Service.temporaryQRCode(pileCode, p);
    }


    @PostMapping("/searchDevice")
    public Callable<Byte> searchDevice(int pileCode, byte p) {
        return () -> ap3000Service.searchDevice(pileCode, p);
    }


    @PostMapping("/mute")
    public Callable<Byte> mute(int pileCode, byte p) {
        return () -> ap3000Service.mute(pileCode, p);
    }


    @PostMapping("/mutiFunction")
    public Callable<Byte> mutiFunction(@RequestBody MutiFunction p) {
        return () -> ap3000Service.mutiFunction(p);
    }


    @PostMapping("/reserveCommand")
    public Callable<Byte> reserveCommand(@RequestBody Reserve p) {
        return () -> ap3000Service.reserveCommand(p);
    }

}

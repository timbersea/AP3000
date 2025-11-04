package com.an.controller;

import com.an.dto.*;
import com.an.service.AP3000Service;
import io.netty.buffer.ByteBuf;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.ByteBuffer;

/**
 * 提供http接口供其它系统调用，无鉴权，仅供内部网络调用
 */
@RestController
public class AP3000Controller {
    @Resource
    AP3000Service ap3000Service;


    @PostMapping("/send")
    public ByteBuf send(int pileCode, byte command, ByteBuffer data) {
        return ap3000Service.send(pileCode, command, data);
    }


    @PostMapping("/syncDeviceStatus")
    public void syncDeviceStatus(int pileCode, byte command) {
        ap3000Service.syncDeviceStatus(pileCode, command);
    }


    @PostMapping("/startChargeCommand")
    public StartChargeResp startChargeCommand(@RequestBody StartCharge p) {
        return ap3000Service.startChargeCommand(p);
    }


    @PostMapping("/modifyChargePara")
    public byte modifyChargePara(@RequestBody ModifyChargeParam p) {
        return ap3000Service.modifyChargePara(p);
    }


    @PostMapping("/powerSettings1")
    public byte powerSettings1(@RequestBody PowerSettings1 p) {
        return ap3000Service.powerSettings1(p);
    }


    @PostMapping("/powerSettings2")
    public byte powerSettings2(@RequestBody PowerSettings2 p) {
        return ap3000Service.powerSettings2(p);
    }


    @PostMapping("/setMaxChargeTimePower")
    public byte setMaxChargeTimePower(@RequestBody MaxTimePower p) {
        return ap3000Service.setMaxChargeTimePower(p);
    }


    @PostMapping("/setUserCard")
    public byte setUserCard(@RequestBody UserCard p) {
        return ap3000Service.setUserCard(p);
    }


    @PostMapping("/resetAndRestart")
    public byte resetAndRestart(int pileCode) {
        return ap3000Service.resetAndRestart(pileCode);
    }


    @PostMapping("/romClean")
    public byte romClean(int pileCode) {
        return ap3000Service.romClean(pileCode);
    }


    @PostMapping("/playVoice")
    public byte playVoice(@RequestBody Voice p) {
        return ap3000Service.playVoice(p);
    }


    @PostMapping("/setDeviceWorkMode")
    public byte setDeviceWorkMode(int pileCode, byte p) {
        return ap3000Service.setDeviceWorkMode(pileCode, p);
    }


    @PostMapping("/deviceUpdatePackage")
    public FirmwareUpdateResp deviceUpdatePackage(@RequestBody FirmwareUpdate p) {
        return ap3000Service.deviceUpdatePackage(p);
    }


    @PostMapping("/deviceUpdatePackageF8")
    public FirmwareUpdateResp deviceUpdatePackageF8(@RequestBody FirmwareUpdateF8 p) {
        return ap3000Service.deviceUpdatePackageF8(p);
    }


    @PostMapping("/query90")
    public PowerSettings1 query90(int pileCode) {
        return ap3000Service.query90(pileCode);
    }


    @PostMapping("/query91")
    public PowerSettings2 query91(int pileCode) {
        return ap3000Service.query91(pileCode);
    }


    @PostMapping("/query92")
    public PowerSettings2 query92(int pileCode) {
        return ap3000Service.query92(pileCode);
    }


    @PostMapping("/query93")
    public UserCard query93(int pileCode) {
        return ap3000Service.query93(pileCode);
    }


    @Deprecated
    @PostMapping("/query94")
    public MaxTimePower query94(int pileCode) {
        return ap3000Service.query94(pileCode);
    }


    @PostMapping("/readEEPROM")
    public ReadEEPROMResp readEEPROM(@RequestBody ReadEEPROM p) {
        return ap3000Service.readEEPROM(p);
    }


    @PostMapping("/writeEEPROM")
    public byte writeEEPROM(@RequestBody WriteEEPROM p) {
        return ap3000Service.writeEEPROM(p);
    }


    @PostMapping("/modifyQRCode")
    public byte modifyQRCode(@RequestBody QRCode p) {
        return ap3000Service.modifyQRCode(p);
    }


    @PostMapping("/setTCMode")
    public byte setTCMode(int pileCode, byte mode) {
        return ap3000Service.setTCMode(pileCode, mode);
    }


    @PostMapping("/stopCharge")
    public StopChargeResp stopCharge(@RequestBody StopCharge p) {
        return ap3000Service.stopCharge(p);
    }


    @PostMapping("/temporaryQRCode")
    public byte temporaryQRCode(int pileCode, ByteBuffer p) {
        return ap3000Service.temporaryQRCode(pileCode, p);
    }


    @PostMapping("/searchDevice")
    public byte searchDevice(int pileCode, byte p) {
        return ap3000Service.searchDevice(pileCode, p);
    }


    @PostMapping("/mute")
    public byte mute(int pileCode, byte p) {
        return ap3000Service.mute(pileCode, p);
    }


    @PostMapping("/mutiFunction")
    public byte mutiFunction(@RequestBody MutiFunction p) {
        return ap3000Service.mutiFunction(p);
    }


    @PostMapping("/reserveCommand")
    public byte reserveCommand(@RequestBody Reserve p) {
        return ap3000Service.reserveCommand(p);
    }

}
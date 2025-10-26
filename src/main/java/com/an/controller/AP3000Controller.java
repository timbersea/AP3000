package com.an.controller;

import com.an.dto.*;
import com.an.service.AP3000Service;
import io.netty.buffer.ByteBuf;
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


    public ByteBuf send(int pileCode, byte command, ByteBuffer data) {
        return ap3000Service.send(pileCode, command, data);
    }


    public void syncDeviceStatus(int pileCode, byte command)  {
        ap3000Service.syncDeviceStatus(pileCode, command);
    }


    public StartChargeResp startChargeCommand(int pileCode, StartCharge p)  {
        return ap3000Service.startChargeCommand(pileCode, p);
    }


    public byte modifyChargePara(int pileCode, ModifyChargeParam p)  {
        return ap3000Service.modifyChargePara(pileCode, p);
    }


    public byte powerSettings1(int pileCode, PowerSettings1 p)  {
        return ap3000Service.powerSettings1(pileCode, p);
    }


    public byte powerSettings2(int pileCode, PowerSettings2 p)  {
        return ap3000Service.powerSettings2(pileCode, p);
    }


    public byte setMaxChargeTimePower(int pileCode, MaxTimePower p)  {
        return ap3000Service.setMaxChargeTimePower(pileCode, p);
    }


    public byte setUserCard(int pileCode, UserCard p)  {
        return ap3000Service.setUserCard(pileCode, p);
    }


    public byte resetAndRestart(int pileCode)  {
        return ap3000Service.resetAndRestart(pileCode);
    }


    public byte romClean(int pileCode)  {
        return ap3000Service.romClean(pileCode);
    }


    public byte playVoice(int pileCode, Voice p)  {
        return ap3000Service.playVoice(pileCode, p);
    }


    public byte setDeviceWorkMode(int pileCode, byte p)  {
        return ap3000Service.setDeviceWorkMode(pileCode, p);
    }


    public FirmwareUpdateResp deviceUpdatePackage(int pileCode, FirmwareUpdate p)  {
        return ap3000Service.deviceUpdatePackage(pileCode, p);
    }


    public FirmwareUpdateResp deviceUpdatePackageF8(int pileCode, FirmwareUpdateF8 p)  {
        return ap3000Service.deviceUpdatePackageF8(pileCode, p);
    }


    public PowerSettings1 query90(int pileCode)  {
        return ap3000Service.query90(pileCode);
    }


    public PowerSettings2 query91(int pileCode)  {
        return ap3000Service.query91(pileCode);
    }


    public PowerSettings2 query92(int pileCode)  {
        return ap3000Service.query92(pileCode);
    }


    public UserCard query93(int pileCode)  {
        return ap3000Service.query93(pileCode);
    }


    @Deprecated
    public MaxTimePower query94(int pileCode)  {
        return ap3000Service.query94(pileCode);
    }


    public ReadEEPROMResp readEEPROM(int pileCode, ReadEEPROM p)  {
        return ap3000Service.readEEPROM(pileCode, p);
    }


    public byte writeEEPROM(int pileCode, WriteEEPROM p)  {
        return ap3000Service.writeEEPROM(pileCode, p);
    }


    public byte modifyQRCode(int pileCode, QRCode p)  {
        return ap3000Service.modifyQRCode(pileCode, p);
    }


    public byte setTCMode(int pileCode, byte mode)  {
        return ap3000Service.setTCMode(pileCode, mode);
    }


    public StopChargeResp stopCharge(int pileCode, StopCharge p)  {
        return ap3000Service.stopCharge(pileCode, p);
    }


    public byte temporaryQRCode(int pileCode, ByteBuffer p)  {
        return ap3000Service.temporaryQRCode(pileCode, p);
    }


    public byte searchDevice(int pileCode, byte p)  {
        return ap3000Service.searchDevice(pileCode, p);
    }


    public byte mute(int pileCode, byte p)  {
        return ap3000Service.mute(pileCode, p);
    }


    public byte mutiFunction(int pileCode, MutiFunction p)  {
        return ap3000Service.mutiFunction(pileCode, p);
    }


    public byte reserveCommand(int pileCode, Reserve p)  {
        return ap3000Service.reserveCommand(pileCode, p);
    }

}

package com.an.idl;

import com.an.net.GlobalContext;
import com.an.net.UDianPackage;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class AP3000ServiceImpl implements AP3000Service.Iface {
    private static final Logger log = LoggerFactory.getLogger(AP3000ServiceImpl.class);

    @Override
    public ByteBuffer send(int physicalId, ByteBuffer data) throws TException {
        return null;
    }

    @Override
    public void syncDeviceStatus(int physicalId, byte command) throws TException {
        UDianPackage uDianPackage = new UDianPackage(physicalId, command, new byte[0]);
        GlobalContext.writeData(physicalId,uDianPackage);
    }

    @Override
    public StartChargeResp startChargeCommand(int physicalId, StartCharge p) throws TException {
        return null;
    }

    @Override
    public byte modifyChargePara(int physicalId, ModifyChargeParam p) throws TException {
        return 0;
    }

    @Override
    public byte powerSettings1(int physicalId, PowerSettings1 p) throws TException {
        return 0;
    }

    @Override
    public byte powerSettings2(int physicalId, PowerSettings2 p) throws TException {
        return 0;
    }

    @Override
    public byte setMaxChargeTimePower(int physicalId, MaxTimePower p) throws TException {
        return 0;
    }

    @Override
    public byte setUserCard(int physicalId, UserCard p) throws TException {
        return 0;
    }

    @Override
    public byte resetAndRestart(int physicalId) throws TException {
        return 0;
    }

    @Override
    public byte romClean(int physicalId) throws TException {
        return 0;
    }

    @Override
    public byte playVoice(int physicalId, Voice p) throws TException {
        return 0;
    }

    @Override
    public byte setDeviceWorkMode(int physicalId, byte p) throws TException {
        return 0;
    }

    @Override
    public FirmwareUpdateResp deviceUpdatePackage(int physicalId, FirmwareUpdate p) throws TException {
        return null;
    }

    @Override
    public FirmwareUpdateResp deviceUpdatePackageF8(int physicalId, FirmwareUpdateF8 p) throws TException {
        return null;
    }

    @Override
    public PowerSettings1 query90(int physicalId) throws TException {
        return null;
    }

    @Override
    public PowerSettings2 query91(int physicalId) throws TException {
        return null;
    }

    @Override
    public PowerSettings2 query92(int physicalId) throws TException {
        return null;
    }

    @Override
    public UserCard query93(int physicalId) throws TException {
        return null;
    }

    @Override
    public MaxTimePower query94(int physicalId) throws TException {
        return null;
    }

    @Override
    public ReadEEPROMResp readEEPROM(int physicalId, ReadEEPROM p) throws TException {
        return null;
    }

    @Override
    public byte writeEEPROM(int physicalId, WriteEEPROM p) throws TException {
        return 0;
    }

    @Override
    public byte modifyQRCode(int physicalId, QRCode p) throws TException {
        return 0;
    }

    @Override
    public byte setTCMode(int physicalId, byte mode) throws TException {
        return 0;
    }

    @Override
    public StopChargeResp stopCharge(int physicalId, StopCharge p) throws TException {
        return null;
    }

    @Override
    public byte temporaryQRCode(int physicalId, ByteBuffer p) throws TException {
        return 0;
    }

    @Override
    public byte searchDevice(int physicalId, byte p) throws TException {
        return 0;
    }

    @Override
    public byte mute(int physicalId, byte p) throws TException {
        return 0;
    }

    @Override
    public byte mutiFunction(int physicalId, MutiFunction p) throws TException {
        return 0;
    }

    @Override
    public byte reserveComman(int physicalId, Reserve p) throws TException {
        return 0;
    }
}

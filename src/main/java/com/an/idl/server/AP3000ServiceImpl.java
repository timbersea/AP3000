package com.an.idl.server;

import com.an.idl.ap3000.*;
import com.an.net.GlobalContext;
import com.an.net.UDianPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
public class AP3000ServiceImpl implements AP3000Service.Iface {
    private static final Logger log = LoggerFactory.getLogger(AP3000ServiceImpl.class);

    @Override
    public ByteBuffer send(int pileCode, ByteBuffer data) throws TException {
        return null;
    }

    @Override
    public void syncDeviceStatus(int pileCode, byte command) throws TException {
        UDianPackage uDianPackage = new UDianPackage(pileCode, command, new byte[0]);
        GlobalContext.asyncWriteData(pileCode, uDianPackage);
    }

    @Override
    public StartChargeResp startChargeCommand(int pileCode, StartCharge p) throws TException {
        ByteBuf toWrite = Unpooled.buffer(35);
        toWrite.writeByte(p.getFeeType());
        toWrite.writeIntLE(p.getBalanceValidateDate());
        toWrite.writeByte(p.getPort());
        toWrite.writeByte(p.getChargeCommand());
        toWrite.writeShortLE(p.getChargeTimeElectric());
        toWrite.writeLongLE(0L);
        toWrite.writeLongLE(p.getOrderNo());
        toWrite.writeShortLE(p.getMaxChargeTime());
        toWrite.writeShortLE(p.getMaxChargePower());
        toWrite.writeByte(p.getQRCodeLight());
        toWrite.writeByte(p.getLongChargeMode());
        toWrite.writeShortLE(p.getExtraChargeTime());
        toWrite.writeByte(p.getSkipShortCircuitCheck());
        toWrite.writeByte(p.getJudgeUserDialOut());
        toWrite.writeByte(p.getFullAutoStop());
        toWrite.writeByte(p.getFullChargePower());
        toWrite.writeByte(p.getFullChargePowerMaxJudgeTime());
        UDianPackage toSend = new UDianPackage(pileCode, (byte) 0x82, toWrite.array());


        UDianPackage response = GlobalContext.requestAndResponse(pileCode, toSend);
        byte[] data = response.getData();
        ByteBuf buffer = Unpooled.buffer(data.length);
        buffer.writeBytes(data);
        StartChargeResp startChargeResp = new StartChargeResp();
        startChargeResp.setResp(buffer.readByte());
        byte[] bytes = new byte[16];
        buffer.readBytes(bytes);
        startChargeResp.setOrderNo(p.getOrderNo());
        startChargeResp.setPort(buffer.readByte());
        startChargeResp.setWaitPort(buffer.readShortLE());
        return startChargeResp;
    }

    @Override
    public byte modifyChargePara(int pileCode, ModifyChargeParam p) throws TException {
        ByteBuf toWrite = Unpooled.buffer(4);
        toWrite.writeByte(p.getFeeType());
        toWrite.writeByte(p.getPort());
        toWrite.writeShortLE(p.getChargeTimeEnerge());
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x8A, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);

        byte[] data = response.getData();
        return data[0];
    }

    @Override
    public byte powerSettings1(int pileCode, PowerSettings1 p) throws TException {
        ByteBuf toWrite = Unpooled.buffer(11);
        toWrite.writeShortLE(p.getPullOutPower());
        toWrite.writeShortLE(p.getPullOutPowerRecognitionTime());
        toWrite.writeByte(p.getFloatChargePercentage());
        toWrite.writeShortLE(p.getFloatChargeStatusRecognitionTime());
        toWrite.writeShortLE(p.getFloatChargeTime());
        toWrite.writeShortLE(p.getHeartbeatReportingInterval());
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x83, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte powerSettings2(int pileCode, PowerSettings2 p) throws TException {
        ByteBuf toWrite = Unpooled.buffer(23);
        toWrite.writeShortLE(p.getDynamicOverloadPower());
        toWrite.writeShortLE(p.getDynamicOverloadRecognitionTime());
        toWrite.writeShortLE(p.getDynamicOverloadStartTime());
        toWrite.writeByte(p.getPullOutInterferencePower());
        toWrite.writeShortLE(p.getPullOutInterferenceRecognitionTime());
        toWrite.writeShortLE(p.getFloatChargeSecondRecognitionTime());
        toWrite.writeShortLE(p.getFloatChargeSecondStatusRecognitionTime());
        toWrite.writeShortLE(p.getMinimumPower());
        toWrite.writeShortLE(p.getMinimumPowerRecognitionTime());
        toWrite.writeShortLE(p.getSecondMaxPowerTime());
        toWrite.writeByte(p.getEnvironmentalAlarmTemperature());
        toWrite.writeByte(p.getPortAlarmTemperature());
        toWrite.writeByte(p.getOpenCloseDetectionUserPullOut());
        toWrite.writeByte(p.getQrCodeLight());

        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x84, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte setMaxChargeTimePower(int pileCode, MaxTimePower p) throws TException {
        ByteBuf toWrite = Unpooled.buffer(4);
        toWrite.writeShortLE(p.getMaxChargeTime());
        toWrite.writeShortLE(p.getMaxChargePower());
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x85, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte setUserCard(int pileCode, UserCard p) throws TException {
        ByteBuf toWrite = Unpooled.buffer(4);
        toWrite.writeByte(p.getUserSector());
        toWrite.writeBytes(p.getUesrCardPassword());
        toWrite.writeBytes(p.getNewCardPassword());
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x86, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte resetAndRestart(int pileCode) throws TException {
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x87, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte romClean(int pileCode) throws TException {
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x88, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte playVoice(int pileCode, Voice p) throws TException {
        ByteBuf toWrite = Unpooled.buffer();
        toWrite.writeByte(p.getBreak());
        toWrite.writeByte(p.getVoiceLength());
        toWrite.writeBytes(p.getVoiceCombination());
        byte[] bytes = new byte[toWrite.readableBytes()];
        toWrite.readBytes(bytes);

        UDianPackage response = new UDianPackage(pileCode, (byte) 0x89, bytes);
        return response.getData()[0];
    }

    @Override
    public byte setDeviceWorkMode(int pileCode, byte p) throws TException {
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x8D, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public FirmwareUpdateResp deviceUpdatePackage(int pileCode, FirmwareUpdate p) throws TException {
        ByteBuf toWrite = Unpooled.buffer(4);
        toWrite.writeShortLE(p.getTotalPackage());
        toWrite.writeShortLE(p.getCurrentPackage());
        toWrite.writeBytes(p.getFirmware());
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0xE1, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        FirmwareUpdateResp firmwareUpdateResp = new FirmwareUpdateResp();

        ByteBuf buffer = Unpooled.buffer(3);
        buffer.writeBytes(response.getData());

        firmwareUpdateResp.setResult(buffer.readByte());
        firmwareUpdateResp.setCurrentPackage(buffer.readShortLE());
        return firmwareUpdateResp;
    }

    @Override
    public FirmwareUpdateResp deviceUpdatePackageF8(int pileCode, FirmwareUpdateF8 p) throws TException {
        ByteBuf toWrite = Unpooled.buffer(4);
        toWrite.writeShortLE(p.getTotalPackage());
        toWrite.writeShortLE(p.getCurrentPackage());
        toWrite.writeBytes(p.getFirmware());
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0xE1, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        FirmwareUpdateResp firmwareUpdateResp = new FirmwareUpdateResp();

        ByteBuf buffer = Unpooled.buffer(3);
        buffer.writeBytes(response.getData());

        firmwareUpdateResp.setResult(buffer.readByte());
        firmwareUpdateResp.setCurrentPackage(buffer.readShortLE());
        return firmwareUpdateResp;
    }

    @Override
    public PowerSettings1 query90(int pileCode) throws TException {

        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x90, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);

        PowerSettings1 powerSettings1 = new PowerSettings1();
        ByteBuf buffer = Unpooled.buffer(response.getData().length);
        powerSettings1.setPullOutPower(buffer.readShortLE());
        powerSettings1.setPullOutPowerRecognitionTime(buffer.readShortLE());
        powerSettings1.setFloatChargePercentage(buffer.readByte());
        powerSettings1.setFloatChargeStatusRecognitionTime(buffer.readShortLE());
        powerSettings1.setFloatChargeTime(buffer.readShortLE());
        powerSettings1.setHeartbeatReportingInterval(buffer.readShortLE());
        return powerSettings1;
    }

    @Override
    public PowerSettings2 query91(int pileCode) throws TException {


        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x91, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        ByteBuf byteBuf = Unpooled.copiedBuffer(response.getData());

        PowerSettings2 powerSettings2 = new PowerSettings2();

        powerSettings2.setDynamicOverloadPower(byteBuf.readShortLE());
        powerSettings2.setDynamicOverloadRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setDynamicOverloadStartTime(byteBuf.readShortLE());
        powerSettings2.setPullOutInterferencePower(byteBuf.readByte());
        powerSettings2.setPullOutInterferenceRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setFloatChargeSecondRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setFloatChargeSecondStatusRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setMinimumPower(byteBuf.readShortLE());
        powerSettings2.setMinimumPowerRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setSecondMaxPowerTime(byteBuf.readShortLE());
        powerSettings2.setEnvironmentalAlarmTemperature(byteBuf.readByte());
        powerSettings2.setPortAlarmTemperature(byteBuf.readByte());
        powerSettings2.setOpenCloseDetectionUserPullOut(byteBuf.readByte());
        powerSettings2.setQrCodeLight(byteBuf.readByte());

        return powerSettings2;
    }

    @Override
    public PowerSettings2 query92(int pileCode) throws TException {


        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x92, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        ByteBuf byteBuf = Unpooled.copiedBuffer(response.getData());

        PowerSettings2 powerSettings2 = new PowerSettings2();

        powerSettings2.setDynamicOverloadPower(byteBuf.readShortLE());
        powerSettings2.setDynamicOverloadRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setDynamicOverloadStartTime(byteBuf.readShortLE());
        powerSettings2.setPullOutInterferencePower(byteBuf.readByte());
        powerSettings2.setPullOutInterferenceRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setFloatChargeSecondRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setFloatChargeSecondStatusRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setMinimumPower(byteBuf.readShortLE());
        powerSettings2.setMinimumPowerRecognitionTime(byteBuf.readShortLE());
        powerSettings2.setSecondMaxPowerTime(byteBuf.readShortLE());
        powerSettings2.setEnvironmentalAlarmTemperature(byteBuf.readByte());
        powerSettings2.setPortAlarmTemperature(byteBuf.readByte());
        powerSettings2.setOpenCloseDetectionUserPullOut(byteBuf.readByte());
        powerSettings2.setQrCodeLight(byteBuf.readByte());

        return powerSettings2;
    }

    @Override
    public UserCard query93(int pileCode) throws TException {


        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x93, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        ByteBuf byteBuf = Unpooled.copiedBuffer(response.getData());
        UserCard userCard = new UserCard();
        userCard.setUserSector(byteBuf.readByte());
        userCard.setUesrCardPassword(byteBuf.readBytes(6).array());
        userCard.setUesrCardPassword(byteBuf.readBytes(6).array());
        return userCard;
    }

    @Deprecated
    @Override
    public MaxTimePower query94(int pileCode) throws TException {
        return null;
    }

    @Override
    public ReadEEPROMResp readEEPROM(int pileCode, ReadEEPROM p) throws TException {

        ByteBuf toWrite = Unpooled.buffer();
        toWrite.writeShortLE(p.getEEPROM());
        toWrite.writeByte(p.getDatalength());
        byte[] bytes = new byte[toWrite.readableBytes()];

        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x8B, bytes);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);

        ReadEEPROMResp readEEPROMResp = new ReadEEPROMResp();
        readEEPROMResp.setIsSuccess(response.getData()[0]);
        byte[] eepromdata = new byte[response.getData().length - 1];
        System.arraycopy(response.getData(), 1, eepromdata, 0, response.getData().length - 1);
        readEEPROMResp.setEEPROMData(eepromdata);
        return readEEPROMResp;
    }

    @Override
    public byte writeEEPROM(int pileCode, WriteEEPROM p) throws TException {


        ByteBuf toWrite = Unpooled.buffer();
        toWrite.writeShortLE(p.getEEPROM());
        toWrite.writeByte(p.getDatalength());
        toWrite.writeBytes(p.getEEPROMDATA());

        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x8C, toWrite.array());
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);

        return response.getData()[0];
    }

    @Override
    public byte modifyQRCode(int pileCode, QRCode p) throws TException {

        ByteBuf toWrite = Unpooled.buffer();
        toWrite.writeByte(p.getMainType());
        toWrite.writeBytes(p.getReserveVaule());
        toWrite.writeBytes(p.getQRCode());

        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x8E, toWrite.array());
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);

        return response.getData()[0];
    }

    @Override
    public byte setTCMode(int pileCode, byte mode) throws TException {
        byte[] bytes = new byte[1];
        bytes[0] = mode;
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x8F, bytes);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);

        return response.getData()[0];
    }

    @Override
    public StopChargeResp stopCharge(int pileCode, StopCharge p) throws TException {
        ByteBuf toWrite = Unpooled.buffer(17);
        toWrite.writeByte(p.getPort());
        toWrite.writeLongLE(0L);
        toWrite.writeLongLE(p.getOrderNo());
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x72, toWrite.array());
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        StopChargeResp stopChargeResp = new StopChargeResp();

        ByteBuf byteBuf = Unpooled.copiedBuffer(response.getData());
        stopChargeResp.setResp(byteBuf.readByte());
        stopChargeResp.setOrderNo(p.getOrderNo());
        return stopChargeResp;
    }

    @Override
    public byte temporaryQRCode(int pileCode, ByteBuffer p) throws TException {
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x95, p.array());

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte searchDevice(int pileCode, byte p) throws TException {
        byte[] bytes = new byte[1];
        bytes[0] = p;
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x96, bytes);

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte mute(int pileCode, byte p) throws TException {
        byte[] bytes = new byte[1];
        bytes[0] = p;
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x97, bytes);

        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte mutiFunction(int pileCode, MutiFunction p) throws TException {
        byte[] bytes = new byte[2];
        bytes[0] = p.getFunction();
        bytes[1] = p.getPort();
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x98, bytes);
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }

    @Override
    public byte reserveComman(int pileCode, Reserve p) throws TException {
        ByteBuf buffer = Unpooled.buffer(12);
        buffer.writeInt(p.R1);
        buffer.writeInt(p.R2);
        buffer.writeInt(p.R3);
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0xFE, buffer.array());
        UDianPackage response = GlobalContext.requestAndResponse(pileCode, uDianPackage);
        return response.getData()[0];
    }
}

package com.an.service;

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
import com.an.net.GlobalContext;
import com.an.net.UDianPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
public class AP3000Service {
    private static final Logger log = LoggerFactory.getLogger(AP3000Service.class);


    /**
     *
     * @param pileCode 设备code
     * @param command  指令编码
     * @param data     指令数据
     * @return
     * @
     */
    public ByteBuf send(int pileCode, byte command, ByteBuffer data) {
        UDianPackage uDianPackage = new UDianPackage(pileCode, command, data.array());
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return Unpooled.buffer(response.getData().length).writeBytes(response.getData());
    }

    /**
     * 查询设备联网状态（81 指令
     *
     */
    public void syncDeviceStatus(int pileCode, byte command) {
        UDianPackage uDianPackage = new UDianPackage(pileCode, command, new byte[0]);
        GlobalContext.asyncWriteData(uDianPackage);
    }

    /**
     * 服务器开始、停止充电操作（82 指令）
     *
     */
    public StartChargeResp startChargeCommand(StartCharge p) {
        ByteBuf toWrite = Unpooled.buffer(35);
        toWrite.writeByte(p.getFeeType());
        toWrite.writeIntLE(p.getBalanceValidateDate());
        toWrite.writeByte(p.getPort());
        toWrite.writeByte(p.getChargeCommand());
        toWrite.writeShortLE(p.getChargeTimeElectric());
        toWrite.writeLongLE(0L);
        toWrite.writeBytes(p.getOrderId());
        toWrite.writeShortLE(p.getMaxChargeTime());
        toWrite.writeShortLE(p.getMaxChargePower());
        toWrite.writeByte(p.getQRCodeLight());
        toWrite.writeByte(p.getLongChargeMode());
        toWrite.writeShortLE(p.getExtraChargeTime());
        toWrite.writeByte(p.getSkipShortCircuitCheck());
        //        toWrite.writeByte(p.getJudgeUserDialOut());
        //        toWrite.writeByte(p.getFullAutoStop());
        //        toWrite.writeByte(p.getFullChargePower());
        //        toWrite.writeByte(p.getFullChargePowerMaxJudgeTime());
        UDianPackage toSend = new UDianPackage(p.getPileCode(), (byte) 0x82, toWrite.array());


        UDianPackage response = GlobalContext.requestAndResponse(toSend);
        byte[] data = response.getData();
        ByteBuf buffer = Unpooled.buffer(data.length);
        buffer.writeBytes(data);
        StartChargeResp startChargeResp = new StartChargeResp();
        startChargeResp.setResp(buffer.readByte());
        byte[] bytes = new byte[16];
        buffer.readBytes(bytes);
        startChargeResp.setOrderId(p.getOrderId());
        startChargeResp.setPort(buffer.readByte());
        startChargeResp.setWaitPort(buffer.readShortLE());
        return startChargeResp;
    }

    /**
     * 服务器修改充电时长/电量（8a 指令
     */
    public byte modifyChargePara(ModifyChargeParam p) {
        ByteBuf toWrite = Unpooled.buffer(4);
        toWrite.writeByte(p.getFeeType());
        toWrite.writeByte(p.getPort());
        toWrite.writeShortLE(p.getChargeTimeEnerge());
        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x8A, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);

        byte[] data = response.getData();
        return data[0];
    }

    /**
     * 设置运行参数 1.1（83 指令
     */
    public byte powerSettings1(PowerSettings1 p) {
        ByteBuf toWrite = Unpooled.buffer(11);
        toWrite.writeShortLE(p.getPullOutPower());
        toWrite.writeShortLE(p.getPullOutPowerRecognitionTime());
        toWrite.writeByte(p.getFloatChargePercentage());
        toWrite.writeShortLE(p.getFloatChargeStatusRecognitionTime());
        toWrite.writeShortLE(p.getFloatChargeTime());
        toWrite.writeShortLE(p.getHeartbeatReportingInterval());
        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x83, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 设置运行参数 1.2（84 指令）
     *
     */
    public byte powerSettings2(PowerSettings2 p) {
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

        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x84, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 设置最大充电时长、最大充电功率（85 指令）
     *
     */
    public byte setMaxChargeTimePower(MaxTimePower p) {
        ByteBuf toWrite = Unpooled.buffer(4);
        toWrite.writeShortLE(p.getMaxChargeTime());
        toWrite.writeShortLE(p.getMaxChargePower());
        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x85, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 设置用户卡参数（86 指令
     *
     */
    public byte setUserCard(UserCard p) {
        ByteBuf toWrite = Unpooled.buffer(4);
        toWrite.writeByte(p.getUserSector());
        toWrite.writeBytes(p.getUesrCardPassword());
        toWrite.writeBytes(p.getNewCardPassword());
        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x86, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 复位重启设备（87 指令
     *
     */
    public byte resetAndRestart(int pileCode) {
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x87, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 存储器清零（88 指令
     *
     */
    public byte romClean(int pileCode) {
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x88, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 播放语音（89 指令）
     *
     */
    public byte playVoice(Voice p) {
        ByteBuf toWrite = Unpooled.buffer();
        toWrite.writeByte(p.getAllowBreak());
        toWrite.writeByte(p.getVoiceLength());
        toWrite.writeBytes(p.getVoiceCombination());
        byte[] bytes = new byte[toWrite.readableBytes()];
        toWrite.readBytes(bytes);

        UDianPackage response = new UDianPackage(p.getPileCode(), (byte) 0x89, bytes);
        return response.getData()[0];
    }

    /**
     * 设置设备的工作模式（8D 指令
     */
    public byte setDeviceWorkMode(int pileCode, byte p) {
        byte[] bytes = new byte[1];
        bytes[0] = p;
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x8D, bytes);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 设备固件升级（E0 升级分机指令）（E1 升级电源板指令）（E2 主机统一升级）（参考 01 指令中的设备类型表
     */
    public FirmwareUpdateResp deviceUpdatePackage(FirmwareUpdate p) {
        return getFirmwareUpdateResp(p.getTotalPackage(), p.getCurrentPackage(), p.getFirmware(), p.getPileCode());
    }

    /**
     * 设备固件升级（F8 指令）
     *
     */
    public FirmwareUpdateResp deviceUpdatePackageF8(FirmwareUpdateF8 p) {
        return getFirmwareUpdateResp(p.getTotalPackage(), p.getCurrentPackage(), p.getFirmware(), p.getPileCode());
    }

    /**
     * 服务器查询当前设备参数（90、91、92、93、94 指令）
     *
     */
    public PowerSettings1 query90(int pileCode) {

        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x90, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);

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

    /**
     * 服务器查询当前设备参数（90、91、92、93、94 指令）
     *
     */
    public PowerSettings2 query91(int pileCode) {


        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x91, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
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

    /**
     * 服务器查询当前设备参数（90、91、92、93、94 指令）
     *
     */
    public PowerSettings2 query92(int pileCode) {


        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x92, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
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

    /**
     * 服务器查询当前设备参数（90、91、92、93、94 指令）
     *
     */
    public UserCard query93(int pileCode) {


        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x93, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        ByteBuf byteBuf = Unpooled.copiedBuffer(response.getData());
        UserCard userCard = new UserCard();
        userCard.setUserSector(byteBuf.readByte());
        userCard.setUesrCardPassword(byteBuf.readBytes(6).array());
        userCard.setUesrCardPassword(byteBuf.readBytes(6).array());
        return userCard;
    }

    /**
     * 服务器查询当前设备参数（90、91、92、93、94 指令）
     *
     */
    @Deprecated
    public MaxTimePower query94(int pileCode) {
        return null;
    }

    /**
     *
     * 服务器读取EEPROM的数据（8B指令）
     */
    public ReadEEPROMResp readEEPROM(ReadEEPROM p) {

        ByteBuf toWrite = Unpooled.buffer();
        toWrite.writeShortLE(p.getEEPROM());
        toWrite.writeByte(p.getDatalength());
        byte[] bytes = new byte[toWrite.readableBytes()];

        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x8B, bytes);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);

        ReadEEPROMResp readEEPROMResp = new ReadEEPROMResp();
        readEEPROMResp.setIsSuccess(response.getData()[0]);
        byte[] eepromdata = new byte[response.getData().length - 1];
        System.arraycopy(response.getData(), 1, eepromdata, 0, response.getData().length - 1);
        readEEPROMResp.setEEPROMData(eepromdata);
        return readEEPROMResp;
    }

    /**
     * 设置设备TC刷卡模式（8C指令）
     *
     */
    public byte writeEEPROM(WriteEEPROM p) {


        ByteBuf toWrite = Unpooled.buffer();
        toWrite.writeShortLE(p.getEEPROM());
        toWrite.writeByte(p.getDatalength());
        toWrite.writeBytes(p.getEEPROMDATA());

        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x8C, toWrite.array());
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);

        return response.getData()[0];
    }

    /**
     * 修改二级码
     *
     */
    public byte modifyQRCode(QRCode p) {

        ByteBuf toWrite = Unpooled.buffer();
        toWrite.writeByte(p.getMainType());
        toWrite.writeBytes(p.getReserveVaule());
        toWrite.writeBytes(p.getQRCode());

        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x8E, toWrite.array());
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);

        return response.getData()[0];
    }

    /**
     * 设置设备TC刷卡模式（8F指令）
     *
     */
    public byte setTCMode(int pileCode, byte mode) {
        byte[] bytes = new byte[1];
        bytes[0] = mode;
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x8F, bytes);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);

        return response.getData()[0];
    }

    /**
     * 充电柜停止充电，但不开柜门（72指令）
     *
     */
    public StopChargeResp stopCharge(StopCharge p) {
        ByteBuf toWrite = Unpooled.buffer(17);
        toWrite.writeByte(p.getPort());
        toWrite.writeLongLE(0L);
        toWrite.writeBytes(p.getOrderNo());
        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x72, toWrite.array());
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        StopChargeResp stopChargeResp = new StopChargeResp();

        ByteBuf byteBuf = Unpooled.copiedBuffer(response.getData());
        stopChargeResp.setResp(byteBuf.readByte());
        stopChargeResp.setOrderNo(p.getOrderNo());
        return stopChargeResp;
    }

    /**
     * 临时二维码（95指令）（带屏设备才有）
     *
     */
    public byte temporaryQRCode(int pileCode, ByteBuffer p) {
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x95, p.array());

        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 声光寻找设备功能（96指令）
     *
     */
    public byte searchDevice(int pileCode, byte p) {
        byte[] bytes = new byte[1];
        bytes[0] = p;
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x96, bytes);

        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 设置运行参数1.3（97指令）
     *
     */
    public byte mute(int pileCode, byte p) {
        byte[] bytes = new byte[1];
        bytes[0] = p;
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0x97, bytes);

        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 多功能指令（98指令）
     *
     */
    public byte mutiFunction(MutiFunction p) {
        byte[] bytes = new byte[2];
        bytes[0] = p.getFunction();
        bytes[1] = p.getPort();
        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0x98, bytes);
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    /**
     * 保留占位指令（FE指令）
     *
     */
    public byte reserveCommand(Reserve p) {
        ByteBuf buffer = Unpooled.buffer(12);
        buffer.writeInt(p.getR1());
        buffer.writeInt(p.getR2());
        buffer.writeInt(p.getR3());
        UDianPackage uDianPackage = new UDianPackage(p.getPileCode(), (byte) 0xFE, buffer.array());
        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        return response.getData()[0];
    }

    private FirmwareUpdateResp getFirmwareUpdateResp(short totalPackage, short currentPackage, byte[] firmware,
                                                     int pileCode) {
        ByteBuf toWrite = Unpooled.buffer(4);
        toWrite.writeShortLE(totalPackage);
        toWrite.writeShortLE(currentPackage);
        toWrite.writeBytes(firmware);
        UDianPackage uDianPackage = new UDianPackage(pileCode, (byte) 0xE1, toWrite.array());

        UDianPackage response = GlobalContext.requestAndResponse(uDianPackage);
        FirmwareUpdateResp firmwareUpdateResp = new FirmwareUpdateResp();

        ByteBuf buffer = Unpooled.buffer(3);
        buffer.writeBytes(response.getData());

        firmwareUpdateResp.setResult(buffer.readByte());
        firmwareUpdateResp.setCurrentPackage(buffer.readShortLE());
        return firmwareUpdateResp;
    }
}

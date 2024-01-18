//package com.an.service;
//
//import com.an.entity.req.Register;
//
//public class RegisterService extends AbstractService<Register>{
//    @Override
//    protected byte[] doService(Register register) {
//        return new byte[0];
//    }
//
//    @Override
//    protected Register parseData(byte[] data) {
//        Register register = new Register();
//        register.setFirmwareVersion((short) ((data[1] << 8) | (data[0] & 0xff)));
//        register.setPortNum(data[2]);
//        register.setVirtualId(data[3]);
//        register.setDeviceType(data[4]);
//        register.setWorkPattern(data[5]);
//        register.setPowerVersion((short) ((data[7] << 8) | (data[6] & 0xff)));
//        return register;
//    }
//}

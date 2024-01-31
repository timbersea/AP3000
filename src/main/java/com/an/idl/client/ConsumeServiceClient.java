package com.an.idl.client;


import com.an.idl.consumer.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

public class ConsumeServiceClient {
    private String serverIP;
    private int port;

    public ConsumeServiceClient(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
    }


    public byte heatBeat(int physicalId, HeatBeat s)  {
        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.heatBeat(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public byte register(int physicalId, Register s) throws TException {
        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.register(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public byte heatBeat21(int physicalId, HeatBeat21 s) throws TException {

        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.heatBeat21(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public SwipingCardResp swipingChard(int physicalId, SwipingCard s) throws TException {

        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.swipingChard(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public byte portChargePowerHeatBeat(int physicalId, PortChargePowerHeatBeat s) throws TException {


        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.portChargePowerHeatBeat(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public byte cabinetHeatBeat(int physicalId, ChargeCabinet s) throws TException {
        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.cabinetHeatBeat(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public byte warnPush(int physicalId, WarnPush s) throws TException {
        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.warnPush(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public byte chargeFinish(int physicalId, ChargeFinish s) throws TException {
        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.chargeFinish(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public byte portStatusPush(int physicalId, PortStatusPush s) throws TException {
        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.portStatusPush(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public byte settleConsume(int physicalId, SettleConsume s) throws TException {
        try (TSocket transport = new TSocket(this.serverIP, port)) {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            ConsumeService.Client client = new ConsumeService.Client(protocol);
            return client.settleConsume(physicalId, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

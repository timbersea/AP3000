package com.an.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SimulatorDevice {
    private static final Logger log = LoggerFactory.getLogger(SimulatorDevice.class);

    public void start() throws Exception {
        Socket socket = new Socket();
        SocketAddress socketAddress = InetSocketAddress.createUnresolved("127.0.0.1", 8080);
        socket.connect(socketAddress);
        String hexString = "3839383630343438313631383730303634383135";
        socket.getOutputStream().write(DatatypeConverter.parseHexBinary(hexString));
        InputStream inputStream = socket.getInputStream();
        int available = inputStream.available();
        while (inputStream.available()!=0){
            byte[] bytes = new byte[available];
            int read = inputStream.read(bytes);
            log.info(DatatypeConverter.printHexBinary(bytes));
        }


    }
}

package de.hs_rm.chat_client.service;

import de.hs_rm.chat_client.model.udp.UdpMessageAck;
import de.hs_rm.chat_client.model.udp.UdpMessagePacket;

import java.io.*;

public class Serializer {

    public static byte[] toBytes(Object obj) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        return byteArrayOutputStream.toByteArray();
    }

    public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        var byteArrayInputStream = new ByteArrayInputStream(bytes);
        var objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }

    public static int getByteSizeOfMessagePacket() {
        try {
            return Serializer.toBytes(new UdpMessagePacket(0, new byte[]{}, false)).length;
        } catch (IOException e) {
            System.err.println("Exception while calculating size of MessagePacket: " + e.getMessage());
        }
        return 0;
    }

    public static int getByteSizeOfMessageAck() {
        try {
            return Serializer.toBytes(new UdpMessageAck(0)).length;
        } catch (IOException e) {
            System.err.println("Exception while calculating size of MessageAck: " + e.getMessage());
        }
        return 0;
    }

}

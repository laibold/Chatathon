package de.hs_rm.chat_client.communication.udp;

import de.hs_rm.chat_client.config.Config;
import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.controller.chat.ChatMessageReceiver;
import de.hs_rm.chat_client.model.udp.UdpMessageAck;
import de.hs_rm.chat_client.model.udp.UdpMessagePacket;
import de.hs_rm.chat_client.service.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class MessageReceiveService {

    public static final int MAXIMUM_SEGMENT_SIZE = Config.MAXIMUM_UDP_SEGMENT_SIZE;
    public static final double ERROR_RATE = Config.UDP_ERROR_RATE;

    private final ClientState clientState;
    private final DatagramSocket receiveSocket;
    private boolean receiveMessages;

    public MessageReceiveService() throws SocketException {
        this.clientState = ClientState.getInstance();

        receiveSocket = new DatagramSocket();
        System.out.println("Started MessageReceiveService with port " + getReceivePort() + "\n");
    }

    public int getReceivePort() {
        return receiveSocket.getLocalPort();
    }

    public void listenForMessages(ChatMessageReceiver receiver) {
        receiveMessages = true;

        new Thread(() -> {
            while (receiveMessages) {
                var receivedData = new byte[MAXIMUM_SEGMENT_SIZE + Serializer.getByteSizeOfMessagePacket()];

                var seqWaitingFor = 0;
                var receivedPackets = new ArrayList<UdpMessagePacket>();
                var receiveMessage = true;

                while (receiveMessage) {

                    // Receive packet
                    var receivedPacket = new DatagramPacket(receivedData, receivedData.length);
                    try {
                        receiveSocket.receive(receivedPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Check if packet comes from current chat partner, otherwise listen for next package
                    var packetAddress = receivedPacket.getAddress().getHostAddress();
                    var currentPartnerAddress = clientState.getCurrentChatPartnerAddress().getHostAddress();
                    if (!packetAddress.equals(currentPartnerAddress) &&
                            receivedPacket.getPort() != clientState.getCurrentChatPartnerPort()) {
                        System.out.println("Received packet from non expected sender, discard.");
                        continue;
                    }

                    // Deserialize to a MessagePacket object
                    UdpMessagePacket packet = null;
                    try {
                        packet = (UdpMessagePacket) Serializer.toObject(receivedPacket.getData());
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    assert packet != null;
                    System.out.println("Received packet with sequence number " + packet.getSeq() + " (last: " + packet.isLast() + ")");

                    if (packet.getSeq() == seqWaitingFor && packet.isLast()) {
                        seqWaitingFor++;
                        receivedPackets.add(packet);
                        System.out.println("-> was last packet");

                        receiveMessage = false;
                    } else if (packet.getSeq() == seqWaitingFor) {
                        seqWaitingFor++;
                        receivedPackets.add(packet);
                        System.out.println("-> stored in buffer");
                    } else {
                        System.out.println("-> discarded (not in order)");
                    }

                    // Create an MessageAck
                    var ack = new UdpMessageAck(seqWaitingFor);
                    byte[] ackBytes = null;
                    try {
                        ackBytes = Serializer.toBytes(ack);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    assert ackBytes != null;
                    var ackPacket = new DatagramPacket(ackBytes, ackBytes.length, receivedPacket.getAddress(), receivedPacket.getPort());

                    if (Math.random() > ERROR_RATE) {
                        try {
                            receiveSocket.send(ackPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("[X] Lost ack with sequence number " + ack.getSeqNumber());
                    }

                    System.out.println("Sending ACK for seq " + (seqWaitingFor - 1) + " with " + ackBytes.length + " bytes");
                }

                System.out.println("Receiving complete, message is:");

                var outputStream = new ByteArrayOutputStream();

                receivedPackets.stream().map(UdpMessagePacket::getData).forEach(p -> {
                    try {
                        outputStream.write(p);
                    } catch (IOException e) {
                        System.err.println("Error reading received bytes: " + e.getMessage());
                    }
                });

                var message = outputStream.toString();
                System.out.println(message + "\n~~~~~~~~~~~~~~~~~~~~~^~~~~~~~~~~~~~~~~~~~~~\n");

                receiver.addIncomingChatMessage(clientState.getCurrentChatPartner(), message);
            }
        }).start();
    }

    public void stopListening() {
        receiveMessages = false;
    }
}

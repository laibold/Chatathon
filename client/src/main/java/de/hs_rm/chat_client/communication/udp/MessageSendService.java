package de.hs_rm.chat_client.communication.udp;

import de.hs_rm.chat_client.config.Config;
import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.model.udp.UdpMessageAck;
import de.hs_rm.chat_client.model.udp.UdpMessagePacket;
import de.hs_rm.chat_client.service.Serializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageSendService {

    public static final int MAXIMUM_SEGMENT_SIZE = Config.MAXIMUM_UDP_SEGMENT_SIZE;
    public static final double ERROR_RATE = Config.UDP_ERROR_RATE;
    public static final int WINDOW_SIZE = 5;
    public static final int TIMER_MS = 300; // TODO abstimmen

    private final ClientState clientState;

    private DatagramSocket sendSocket;
    private InetAddress receiverAddress;
    private int receiverPort;
    private int lastSentSeq;

    public MessageSendService() {
        this.clientState = ClientState.getInstance();
    }

    public void sendMessage(String message) throws IOException, ClassNotFoundException {
        this.receiverAddress = clientState.getCurrentChatPartnerAddress();
        this.receiverPort = clientState.getCurrentChatPartnerPort();
        sendSocket = new DatagramSocket(); // TODO in den Konstruktor packen

        lastSentSeq = 0;

        // Sequence number of the last ack'ed packet
        var lastAckedSeq = 0;

        var messageBytes = message.getBytes();
        System.out.println("Data size: " + messageBytes.length + " bytes");

        // Last packet sequence number
        var numberOfSeqs = (int) Math.ceil((double) messageBytes.length / MAXIMUM_SEGMENT_SIZE);

        System.out.println("Number of packets to send: " + numberOfSeqs);

        // List of all the packets sent
        var sentPackets = new ArrayList<UdpMessagePacket>();

        // Sending loop
        while (true) {
            // Noch Platz im aktuellen Fenster && noch nicht alle geschickt
            while (lastSentSeq - lastAckedSeq < WINDOW_SIZE && lastSentSeq < numberOfSeqs) {

                var bytesToSend = new byte[MAXIMUM_SEGMENT_SIZE];

                // Copy segment of data bytes to array
                var fromByte = lastSentSeq * MAXIMUM_SEGMENT_SIZE;
                var untilByte = fromByte + MAXIMUM_SEGMENT_SIZE;
                bytesToSend = Arrays.copyOfRange(messageBytes, fromByte, untilByte);

                // Create MessagePacket
                var isLastPacket = lastSentSeq == numberOfSeqs - 1;
                var messagePacket = new UdpMessagePacket(lastSentSeq, bytesToSend, isLastPacket);

                // Add packet to the list of sent packets
                sentPackets.add(messagePacket);

                // SEND
                sendMessagePacket(messagePacket);
                lastSentSeq++;
            } // End of sending while

            // Creating packet for the ACK
            var ackBytes = new byte[Serializer.getByteSizeOfMessageAck()];
            var ack = new DatagramPacket(ackBytes, ackBytes.length);

            try {
                // Set timeout after which the code continues in the catch clause
                sendSocket.setSoTimeout(TIMER_MS);
                sendSocket.receive(ack);

                // Deserialize the model.RDTAck object
                var ackObject = (UdpMessageAck) Serializer.toObject(ack.getData());

                System.out.println("Received ACK for " + ackObject.getSeqNumber());

                // break if last packet was ack'ed
                if (ackObject.getSeqNumber() == numberOfSeqs) {
                    break;
                }

                // set lastAckedSeq to ack's seqNumber if it's higher than current value
                lastAckedSeq = Math.max(lastAckedSeq, ackObject.getSeqNumber());
            } catch (SocketTimeoutException e) {
                // then send all the sent but non-acked packets
                for (var i = lastAckedSeq; i < lastSentSeq; i++) {
                    sendMessagePacket(sentPackets.get(i));
                }
            }
        }

        System.out.println("Finished transmission");
    }

    private void sendMessagePacket(UdpMessagePacket messagePacket) throws IOException {
        var dataToSend = Serializer.toBytes(messagePacket);
        var packet = new DatagramPacket(dataToSend, dataToSend.length, receiverAddress, receiverPort);

        System.out.println("sending packet with sequence number " + lastSentSeq + " and size " + dataToSend.length +
            " bytes to " + receiverAddress.getHostAddress() + ":" + receiverPort);

        if (Math.random() > ERROR_RATE) {
            sendSocket.send(packet);
        } else {
            System.out.println("[X] Lost packet with sequence number " + lastSentSeq);
        }
    }

}

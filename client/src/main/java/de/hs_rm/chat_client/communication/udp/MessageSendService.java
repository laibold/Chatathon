package de.hs_rm.chat_client.communication.udp;

import de.hs_rm.chat_client.config.Config;
import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.controller.chat.ChatErrorHandler;
import de.hs_rm.chat_client.model.udp.UdpMessageAck;
import de.hs_rm.chat_client.model.udp.UdpMessagePacket;
import de.hs_rm.chat_client.service.Serializer;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageSendService {

    public static final int MAXIMUM_SEGMENT_SIZE = Config.MAXIMUM_UDP_SEGMENT_SIZE;
    public static final double ERROR_RATE = Config.UDP_ERROR_RATE;
    public static final int WINDOW_SIZE = 5;
    public static final int TIMER_MS = 50;
    private static final int SENDING_TIMEOUT_COUNTER = 10;

    private final ClientState clientState;

    private final ChatErrorHandler chatErrorHandler;

    private DatagramSocket sendSocket;
    private InetAddress receiverAddress;
    private int receiverPort;
    private int lastSentSeq;
    private final SendingTimeoutChecker sendingTimeoutChecker = new SendingTimeoutChecker();

    public MessageSendService(ChatErrorHandler chatErrorHandler) {
        this.clientState = ClientState.getInstance();
        this.chatErrorHandler = chatErrorHandler;

        try {
            this.sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            chatErrorHandler.notifyAboutSendingError("Chat connection could not be established. Please try to restart");
        }
    }

    public void sendMessage(String message) {
        this.receiverAddress = clientState.getCurrentChatPartnerAddress();
        this.receiverPort = clientState.getCurrentChatPartnerPort();

        // Sending loop
        new Thread(() -> {
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

            while (true) {
                // Still space in current window && didn't send all packages
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
                    // Set timeout after which the code continues in the SocketTimeoutException catch clause
                    sendSocket.setSoTimeout(TIMER_MS);

                    sendSocket.receive(ack);

                    // Deserialize the UdpMessageAck object
                    UdpMessageAck ackObject = null;
                    try {
                        ackObject = (UdpMessageAck) Serializer.toObject(ack.getData());
                    } catch (ClassNotFoundException | IOException ignore) {
                    }

                    assert ackObject != null;
                    System.out.println("Received ACK for " + ackObject.getSeqNumber());

                    // break if last packet was ack'ed
                    if (ackObject.getSeqNumber() == numberOfSeqs) {
                        break;
                    }

                    // set lastAckedSeq to ack's seqNumber if it's higher than current value
                    lastAckedSeq = Math.max(lastAckedSeq, ackObject.getSeqNumber());
                } catch (SocketTimeoutException e) {
                    // re-send all the sent but non-acked packets
                    var windowToSend = new ArrayList<UdpMessagePacket>();
                    for (var i = lastAckedSeq; i < lastSentSeq; i++) {
                        var packetToSend = sentPackets.get(i);
                        windowToSend.add(packetToSend);
                        sendMessagePacket(packetToSend);
                    }

                    // check if sent window should lead to timeout
                    try {
                        sendingTimeoutChecker.countSend(windowToSend);
                    } catch (SocketTimeoutException socketTimeoutException) {
                        chatErrorHandler.notifyAboutSendingError("Timeout sending message. Please meet your partner personally");
                        break;
                    }
                } catch (IOException e) {
                    // IOException from Socket
                    chatErrorHandler.notifyAboutSendingError("Error sending message. Please meet your partner personally");
                    break;
                }
            }
        }).start();

        System.out.println("Finished transmission");
    }

    private void sendMessagePacket(UdpMessagePacket messagePacket) {
        byte[] dataToSend = null;

        try {
            dataToSend = Serializer.toBytes(messagePacket);
        } catch (IOException e) {
            chatErrorHandler.notifyAboutSendingError("Error sending message. Please try again");
        }

        assert dataToSend != null;
        var packet = new DatagramPacket(dataToSend, dataToSend.length, receiverAddress, receiverPort);

        System.out.println("sending packet with sequence number " + messagePacket.getSeq() + " and size " + dataToSend.length +
                " bytes to " + receiverAddress.getHostAddress() + ":" + receiverPort);

        if (Math.random() > ERROR_RATE) {
            try {
                sendSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[X] Lost packet with sequence number " + lastSentSeq);
        }
    }

    private static class SendingTimeoutChecker {
        private final ArrayList<UdpMessagePacket> currentWindow = new ArrayList<>();
        private int sendCounter = 0;

        /**
         * @throws SocketTimeoutException when all packets in window have been sent SENDING_TIMEOUT_COUNTER times directly after another
         */
        public void countSend(ArrayList<UdpMessagePacket> window) throws SocketTimeoutException {
            if (currentWindow.containsAll(window)) {
                sendCounter++;
                if (sendCounter == SENDING_TIMEOUT_COUNTER) {
                    throw new SocketTimeoutException();
                }
            } else {
                currentWindow.addAll(window);
                sendCounter = 1;
            }
        }
    }

}

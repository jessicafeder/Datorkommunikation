package Lab1;

import java.io.IOException;
import java.net.*;

public class Main {

    public static void main(String[] args) {

        String[] server = new String[4];
        server[0] = "svl2.ntp.se";
        server[1] = "gbg2.ntp.se";
        server[2] = "sth1.ntp.se";
        server[3] = "mmo2.ntp.se";

        try {
            DatagramSocket socket = new DatagramSocket();
            boolean run = true;
            int i = 0;

            SNTPMessage msg = new SNTPMessage();
            byte[] buf = msg.toByteToArray();
            SNTPMessage response = new SNTPMessage(buf);
            socket.setSoTimeout(5);
            while (run) {
                InetAddress address = InetAddress.getByName(server[i++]);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 123);
                socket.send(packet);
                System.out.println("Sent request to server: " + address.getHostName());

                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException e) {
                    System.out.println("Could not get a response in time, trying another server.");
                }

                response = new SNTPMessage(packet.getData());

                if (response.getMode() == 4) {
                    System.out.println("Message received from server: " + packet.getAddress().getHostName() + ": " + packet.getPort());
                    run = false;
                } else if (i == server.length) {
                    i = 0;
                }
            }

            socket.close();
            System.out.println("Connection closed to server");

            response.printDataToConsole();

            calculateOffset(response);

            System.out.println(response.toString());


        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private static void calculateOffset(SNTPMessage message) {

        double t1 = message.getOriginateTimeStamp();
        double t2 = message.getReceiveTimeStamp();
        double t3 = message.getTransmitTimeStamp();
        double t4 = message.getReferenceTimeStamp();

        double delay = (t4 - t1) - (t3 - t2);
        double offset = ((t2 - t1) + (t3 - t4)) / 2;

        System.out.println("Server offset: " + offset + " seconds");
    }


}


package Lab1;

import java.io.IOException;
import java.net.*;

public class Main {

    public static void main(String[] args) {

        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("gbg1.ntp.se");
            SNTPMessage msg = new SNTPMessage();
            byte [] buf = msg.toByteToArray();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 123);
            socket.send(packet);
            System.out.println("Sent request");
            socket.receive(packet);
            SNTPMessage response = new SNTPMessage(packet.getData());
            System.out.println("Got reply");
            socket.close();
            System.out.println();


        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }





        /*byte [] buf = {     36, 1,  0,  -25,
                            0,  0,  0,  0, //48 bytes
                            0,  0,  0,  2,
                            80, 80, 83, 0,
                          -29, 116,  5, 61,  0,  0,    0,   0,
                          -29, 116,  5, 59, 14, 86,    0,   0,
                          -29, 116,  5, 62,  0, 47, -121, -38,
                          -29, 116,  5, 62,  0, 47, -113,  -1};*/

        // Första byten är 36 och innehåller:
        // LI   VN  Mode
        //  0    4    4 (server mode)
        // 01  234  567
        // 00  100  100 (Binärt tal)

        /*SNTPMessage msg = new SNTPMessage(buf);*/
    }
}

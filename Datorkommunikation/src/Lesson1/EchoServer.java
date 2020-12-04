package Lesson1;

import java.io.IOException;
import java.net.*;

public class EchoServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public EchoServer() throws SocketException {
        socket = new DatagramSocket(4445);
    }
/*  Creating a while loop that runs until running is changed to false by some error or a termination message from the client */
    public void run() {
        running = true;

        while(running){
/* Instantiate a DatagramPacket to receive incoming msg */
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
/* Calling the receive method on the socket, blocked until a msg arrives and it stores the msg inside the byte array of the DatagramPacket. */
            try {
                socket.receive(packet);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            /* After receiving the msg, we retrieve the address and port of the client since we are going to send the response back */
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
/*Next, we create a DatagramPacket for sending a message to the client. Notice the difference in signature with the receiving
packet. This one also requires address and port of the client we are sending the message to.*/
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());

            if(received.equals("end")){
                running = false;
                continue;
            }
            try {
                socket.send(packet);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        socket.close();
    }


    public static class EchoClient{
        private DatagramSocket socket;
        private InetAddress address;
        private byte[] buf;

        public EchoClient() throws UnknownHostException, SocketException {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        }

        public String sendEcho(String msg) throws IOException {
            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            return received;
        }

        public void close(){
            socket.close();
        }
    }
    public static void main(String[] args) throws SocketException {
        new EchoServer();
    }
}

package Lesson1;

import java.net.*;
import java.io.*;

public class EchoServerTest {

    // TODO Skapa en server socker som lyssnar på en angiven port på server
    // TODO Acceptera anslutningar från en klient
    // TODO Läsa data från klienten
    // TODO skriva samma data till klienten
    // TODO kör programmet och starta Putty, skriv hostname (localhost) och Port (8080) + RAW
    // TODO samma data skrivs tillbaka till den som skriver in data, till Putty
    // TODO inlogg



    public static void main(String[] args) {
        int portNumber = 8080;

        try {
       //TODO Skapa en server socker som lyssnar på en angiven port på server
            ServerSocket serverSocket = new ServerSocket(portNumber);
        //TODO Acceptera anslutningar från en klient
            Socket clientSocket = serverSocket.accept();
        // TODO Läsa data från klienten
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // TODO skriva samma data till klienten
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            out.println("Enter username: ");

            while((inputLine = in.readLine()) != null){
                System.out.println(inputLine);
                if(inputLine.equals("Jessica")){
                    out.println("Enter password: ");
                    inputLine = in.readLine();
                } if(inputLine.equals("1234")){
                    out.println("Access granted!");
                } else{
                    out.println("Access denied!");
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
package Lab1;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class SNTPMessage {
    private byte leapIndicator = 0;
    private byte versionNumber = 4; //SNTP version is 4 (server)
    private byte mode = 0;
    private short stratum = 0;
    private short pollInterval = 0;
    private byte precision = 0;

    /*Första byten 36
     | 0 1 | 2 3 4 | 5 6 7 |
     | LI  |  VN   |  Mode |
       0 0   1 0 0   1 0 0 - (00100100) byte kod
        0      4       4
    */

    private double rootDelay = 0;
    private double rootDispersion = 0;
    /*
    rootDelay 32-bit signed fixed-point number
    fraction point between 15 and 16
    0000 0000 0000 0000 . 0000 0000 0000 0000
    Same for rootDispersion
     */

    private byte[] referenceIdentifier = {0, 0, 0, 0};
    /* Reference identifier 32-bits
    80,  80, 83,   0,
     P,   P,  S,   0
    */
    private double referenceTimeStamp = 0;
    private double originateTimeStamp = 0;
    private double receiveTimeStamp = 0;
    private double transmitTimeStamp = 0;


    public SNTPMessage(byte[] buf) {
        byte b = buf[0];

         /* b = 36
        | 0 1 | 2 3 4 | 5 6 7 |
        | LI  |  VN   |  Mode |
          0 0   1 0 0   1 0 0 - (00100100) byte kod
           0      4       4
        */

        leapIndicator = (byte) ((b >> 6) & 0x3);
        //00100100 -> 0010 0100
        // >> skiftar alla bits 6 steg till höger
        //0001 0010 b>>1
        //0000 1001 b>>2 osv... osv...
        //0000 0000 Resultat av b>>6
        //0x3? -> 0011
        // 36decimalt -> 24hex -> 0010 0100 binärt
        versionNumber = (byte) ((b >> 3) & 0x7);
        //skifta 3 steg till höger
        // 0010 0100 -> 0000 0100
        // & 0x7?
        //0000 0100 -> 0000 0111
        mode = (byte) (b & 0x7);
        // 0010 0100

        stratum = unsignedByteToShort(buf[1]); // 1
        pollInterval = unsignedByteToShort(buf[2]); // 0
        precision = buf[3]; // -25

        //Vi får datan för rootDelay som 4 bytes d.v.s 32 bits i följd
        //00000000 00000000 00000000 00000000 | 00000000 00000000 00000000 00000000 -> 0 - 65535 | 0 - 65535 -> 0 - 65535,65535
        //Exempel: 1000 0100 0110 0010 | 0110 0100 0110 0010 -> 33890.25698
        // buf[4] = 132
        // buf[5] = 98
        // buf[6] = 100 (1000 0000)
        // buf[7] = 98
        // 256.0 -> 0xFF+1.0
        // 65536.0 -> 0xFFFF+1.0
        rootDelay = (((buf[4] * 256.0)
                + (unsignedByteToShort(buf[5])))
                + ((unsignedByteToShort(buf[6]) / (0xFF + 1.0))
                + (unsignedByteToShort(buf[7]) / (0xFFFF + 1.0)))); // 0,   0,  0,   0,

        rootDispersion = (((buf[8] * 255.0)
                + (unsignedByteToShort(buf[9])))
                + ((unsignedByteToShort(buf[10]) / (0xFF + 1.0))
                + (unsignedByteToShort(buf[11]) / (0xFFFF + 1.0)))); // 0,   0,  0,   2,

        // 0101 0000 | 0101 0000 | 0101 0011 | 0000 0000
        //     80          80         83          0
        //ASCII 80, 80, 83, 0 -> PPS
        referenceIdentifier[0] = buf[12]; // referenceIdentifier = 80, 80, 83, 0,
        referenceIdentifier[1] = buf[13];
        referenceIdentifier[2] = buf[14];
        referenceIdentifier[3] = buf[15];


        //Timestamps is 64bits... instead of 32 we did earlier but still the same practice.
        referenceTimeStamp = byteArrayToDouble(buf, 16); // -29, 116,  5,  61,  0,  0,    0,   0,
        originateTimeStamp = byteArrayToDouble(buf, 24); // -29, 116,  5,  61,  0,  0,    0,   0,
        receiveTimeStamp = byteArrayToDouble(buf, 32); // -29, 116,  5,  62,  0, 47, -121, -38,
        transmitTimeStamp = byteArrayToDouble(buf, 40); // -29, 116,  5,  62,  0, 47, -113,  -1}
    }

    public SNTPMessage() {
        //Mode to 3 for client
        mode = 3;
        transmitTimeStamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0; // omvandlar milisekunder till sekunder
    }

    private double byteArrayToDouble(byte[] buf, int index) {
        double result = 0.0;
        for (int i = 0; i < 8; i++) {
            result += unsignedByteToShort(buf[index + i]) * Math.pow(2, (3 - i) * 8);
        }
        return result;
    }

    private void doubleToByteArray(byte[] array, int index, double data) {
        for (int i = 0; i < 8; i++) {
            array[index + i] = (byte) (data / Math.pow(2, (3 - i) * 8));
            data -= (double) (unsignedByteToShort(array[index + i]) * Math.pow(2, (3 - i) * 8));
        }
    }

    public void printDataToConsole() throws UnsupportedEncodingException {
        System.out.println();
        System.out.println("--Printing data from the message--");
        System.out.println("Leap Indicator: " + leapIndicator);
        System.out.println("Version Number: " + versionNumber);
        System.out.println("Mode: " + mode);
        System.out.println("Stratum: " + stratum);
        System.out.println("Poll Interval: " + pollInterval);
        System.out.println("Precision: " + precision);
        System.out.println("Root Delay: " + rootDelay);
        System.out.println("Root Dispersion: " + rootDispersion);
        System.out.println("Reference Identifier: " + new String(referenceIdentifier, StandardCharsets.US_ASCII));
        System.out.println("Reference TimeStamp: " + referenceTimeStamp);
        System.out.println("Originate TimeStamp: " + originateTimeStamp);
        System.out.println("Receive Time Stamp: " + referenceTimeStamp);
        System.out.println("Transmit Time Stamp: " + transmitTimeStamp);
        System.out.println();
        System.out.println("-----------------------");
        System.out.println();
    }

    public double getReferenceTimeStamp() {
        return this.referenceTimeStamp;
    }

    public double getOriginateTimeStamp() {
        return this.originateTimeStamp;
    }

    public double getReceiveTimeStamp() {
        return this.receiveTimeStamp;
    }

    public double getTransmitTimeStamp() {
        return this.transmitTimeStamp;
    }

    public byte getMode() {
        return this.mode;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("Leap Indicator: ").append(leapIndicator).append("\n");
        msg.append("Version Number: ").append(versionNumber).append("\n");
        msg.append("Mode: ").append(mode).append("\n");
        msg.append("Stratum: ").append(stratum).append("\n");
        msg.append("Poll Interval: ").append(pollInterval).append("\n");
        msg.append("Precision: ").append(precision).append("\n");
        msg.append("Root Delay: ").append(rootDelay).append("\n");
        msg.append("Root Dispersion: ").append(rootDispersion).append("\n");
        msg.append("Reference Identifier: ").append(new String(referenceIdentifier, StandardCharsets.US_ASCII)).append("\n");
        msg.append("Reference Time Stamp: ").append(referenceTimeStamp).append("\n");
        msg.append("Originate Time Stamp: ").append(originateTimeStamp).append("\n");
        msg.append("Receive Time Stamp: ").append(referenceTimeStamp).append("\n");
        msg.insert(msg.length(), "Transmit Time Stamp: " + transmitTimeStamp + "\n");
        return msg.toString();
    }


    public byte[] toByteToArray() {
        byte[] array = new byte[48];
        array[0] = (byte) ((byte) (leapIndicator << 6) | versionNumber << 3 | mode);
        //LI == 0
        //00 << 6 -> 0000 0000
        //Version number == 4
        // 0100 << 3 -> 0010 0000
        // mode == 3
        // 0011
        array[1] = (byte) stratum;
        array[2] = (byte) pollInterval;
        array[3] = precision;

        int data = (int) (rootDelay * (0xff + 1));
        array[4] = (byte) ((data >> 24) & 0xff);
        array[5] = (byte) ((data >> 16) & 0xff);
        array[6] = (byte) ((data >> 8) & 0xff);
        array[7] = (byte) (data & 0xff);

        int rd = (int) (rootDispersion * (0xff + 1));
        array[8] = (byte) ((rd >> 24) & 0xff);
        array[9] = (byte) ((rd >> 16) & 0xff);
        array[10] = (byte) ((rd >> 8) & 0xff);
        array[11] = (byte) (rd & 0xff);

        array[12] = referenceIdentifier[0];
        array[13] = referenceIdentifier[1];
        array[14] = referenceIdentifier[2];
        array[15] = referenceIdentifier[3];

        doubleToByteArray(array, 16, referenceTimeStamp);
        doubleToByteArray(array, 24, originateTimeStamp);
        doubleToByteArray(array, 32, receiveTimeStamp);
        doubleToByteArray(array, 40, transmitTimeStamp);

        return array;
    }


    private short unsignedByteToShort(byte b) {
        //signed byte tal mellan -126 - 127
        //unsigned byte tal mellan 0 - 255
        //Exempel b = 1101 1001, översta biten är satt och java tolkar som ett negativt tal
        //Kolla om översta biten är satt genom bitvis and med 0x80 eller 1000 0000

        if (((b >> 7) & 0x1) == 1) {
            return (short) ((b & 0x7F) + 128); // 0111 1111 -> 64+32+16+8+4+2+1 = 127 -> 0x7F hexadecimal, 128 tack vare första biten
        } else {
            return (short) ((b & 0xFF));
        }

    }
}

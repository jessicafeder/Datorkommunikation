package Lab1;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class SNTPMessage {
    private byte leapIndicator = 0;
    private byte versionNumber = 4;
    private byte mode = 0;
    private short stratum = 0;
    private short pollInterval = 0;
    private byte precision = 0;
    private double rootDelay = 0;
    private double rootDispersion = 0;

    private byte[] referenceIdentifier = {0, 0, 0, 0};

    private double referenceTimeStamp = 0;
    private double originateTimeStamp = 0;
    private double receiveTimeStamp = 0;
    private double transmitTimeStamp = 0;


    public SNTPMessage(byte[] buf) {
        byte b = buf[0];

        leapIndicator = (byte) ((b >> 6) & 0x3);
        versionNumber = (byte) ((b >> 3) & 0x7);
        mode = (byte) (b & 0x7);
        stratum = unsignedByteToShort(buf[1]);
        pollInterval = unsignedByteToShort(buf[2]);
        precision = buf[3];
        rootDelay = (((buf[4] * 256.0)
                + (unsignedByteToShort(buf[5])))
                + ((unsignedByteToShort(buf[6]) / (0xFF + 1.0))
                + (unsignedByteToShort(buf[7]) / (0xFFFF + 1.0))));

        rootDispersion = (((buf[8] * 255.0)
                + (unsignedByteToShort(buf[9])))
                + ((unsignedByteToShort(buf[10]) / (0xFF + 1.0))
                + (unsignedByteToShort(buf[11]) / (0xFFFF + 1.0))));

        referenceIdentifier[0] = buf[12];
        referenceIdentifier[1] = buf[13];
        referenceIdentifier[2] = buf[14];
        referenceIdentifier[3] = buf[15];

        referenceTimeStamp = byteArrayToDouble(buf, 16);
        originateTimeStamp = byteArrayToDouble(buf, 24);
        receiveTimeStamp = byteArrayToDouble(buf, 32);
        transmitTimeStamp = byteArrayToDouble(buf, 40);
    }

    public SNTPMessage() {
        mode = 3;
        transmitTimeStamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;
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
            data -= unsignedByteToShort(array[index + i]) * Math.pow(2, (3 - i) * 8);
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
        if (((b >> 7) & 0x1) == 1) {
            return (short) ((b & 0x7F) + 128);
        } else {
            return (short) ((b & 0xFF));
        }

    }
}

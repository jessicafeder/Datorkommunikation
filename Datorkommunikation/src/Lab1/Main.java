package Lab1;

public class Main {
//Wait for instructions

    public static void main(String[] args) {
        byte [] buf = {     36, 1,  0,  -25,
                            0,  0,  0,  0, //48 bytes
                            0,  0,  0,  2,
                            80, 80, 83, 0,
                          -29, 116,  5, 61,  0,  0,    0,   0,
                          -29, 116,  5, 59, 14, 86,    0,   0,
                          -29, 116,  5, 62,  0, 47, -121, -38,
                          -29, 116,  5, 62,  0, 47, -113,  -1};

        // Första byten är 36 och innehåller:
        // LI   VN  Mode
        //  0    4    4 (server mode)
        // 01  234  567
        // 00  100  100 (Binärt tal)

        SNTPMessage msg = new SNTPMessage(buf);
    }
}

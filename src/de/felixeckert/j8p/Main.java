package de.felixeckert.j8p;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("KOMMANDANT 128 | J8P/J8V //");
        System.out.println("v1.0 by Felix Eckert     //");
        System.out.println("///////////////////////////");

        // Load a Program ROM if none is specified in the args
        if (args.length == 0) {
            System.out.println();
            System.out.println("You have not specified any ROM to be executed.");
            System.out.println("Enter a path to a ROM you wish to load>");
            Scanner s = new Scanner(System.in);
            String path = s.nextLine();

            System.out.printf("Loading ROM \"%s\"...\n", path);
        }

        // LDM #$2
        J8P.program[0] = 21;
        J8P.program[1] = (byte) 0x2;
        J8P.program[2] = 1;

        // LDS #$9a
        J8P.program[3] = 3;
        J8P.program[4] = (byte) 0xe;
        J8P.program[5] = 1;

        // LDT #$9a
        J8P.program[6] = 5;
        J8P.program[7] = (byte) 0x00;
        J8P.program[8] = 1;

        // LDU #$9a
        J8P.program[9] = 7;
        J8P.program[10] = (byte) 0xff;
        J8P.program[11] = 1;

        // PCL $deea
        J8P.program[12] = 29;
        J8P.program[13] = (byte) 0x00;
        J8P.program[14] = (byte) 0x00;

        // JMP #$0
        J8P.program[15] = 0x1e;
        J8P.program[16]  = 0x17;

        // END
        J8P.program[15] = (byte) 63;

        J8P.general[34] = (byte) 0xa8;

        J8P.plength = 3;

        while (J8P.status != 0x7e) {
            J8P.step();
            J8V.step();
        }
        J8V.step();

        System.out.println("=======================");
        System.out.println("Register Dump: ");
        byte[] regLookup = new byte[] {J8P.accumulator, J8P.indexes[0], J8P.indexes[1], J8P.indexes[2], J8P.indexes[3], J8P.indexes[4], J8P.indexes[5], J8P.indexes[6], J8P.indexes[7], J8P.mspptr, J8P.status};
        for (int i = 0; i < regLookup.length; i++) {
            System.out.printf("Register No. %s , value: %s \n", i, regLookup[i]);
        }
        System.out.println("=======================");

        System.out.println("Press ENTER to exit!");
        System.in.read();
        System.exit(J8P.status);
    }
}

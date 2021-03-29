package de.felixeckert.j8p;

import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
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
            File file = new File(path);
            while (true) {

                System.out.printf("Loading ROM \"%s\"...\n", path);
                if (file.exists()) {
                    break;
                } else {
                    System.out.println("That file does not exist!");
                    System.out.println("Enter a path to a ROM you wish to load>");
                    path = s.nextLine();
                }
            }

            byte[] data = Files.readAllBytes(file.toPath().toAbsolutePath());
            for (int i = 0; i < data.length; i++) {
                J8P.program[i] = data[i];
                System.out.printf("Instruction %s/%s \r", i, data.length-1);
            }
        }

        // END
        J8P.program[J8P.program.length-1] = (byte) 63;

        J8P.plength = 3;

        while (J8P.status != 0x7e && J8P.status != 0x7f) {
            J8P.step();
            J8V.step();
        }
        J8V.stop();

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

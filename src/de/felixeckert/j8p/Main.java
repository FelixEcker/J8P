package de.felixeckert.j8p;

import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {
    static boolean logan = false;
    static boolean stack = false;
    static boolean regis = false;

    public static void main(String[] args) throws Exception {
        System.out.println("KOMMANDANT 128 | J8P/J8V //");
        System.out.println("v1.0 by Felix Eckert     //");
        System.out.println("///////////////////////////");

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.matches("-f")) {
                File file = new File(args[i+1]);

                byte[] data = Files.readAllBytes(file.toPath().toAbsolutePath());
                for (int j = 0; j < data.length; j++) {
                    J8P.program[j] = data[j];
                    System.out.printf("Instruction %s/%s \r", j+1, data.length);
                }
                System.out.println();

                i++;
            } else if (arg.matches("-printStack")) {
                logan = true;
                stack = true;
            } else if (arg.matches(("-printRegs"))) {
                logan = true;
                regis = true;
            }
        }

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
                System.out.printf("Instruction %s/%s \r", i+1, data.length);
            }
            System.out.println();
        }

        // END
        J8P.program[J8P.program.length-1] = (byte) 63;

        while (J8P.status != 0x7e && J8P.status != 0x7f) {
            J8P.step();
            J8V.step();
        }
        J8V.stop();

        if (logan) {
            System.out.println("=======================");
        }

        if (stack) printStack();
        if (regis) printRegs();

        if (logan) {
            System.out.println("=======================");
        }

        System.out.println("Press ENTER to exit!");
        System.in.read();
        System.exit(J8P.status);
    }

    public static void printStack() {
        System.out.println("Stack:");
        for (int i = 0; i < J8P.calstk.length; i++) {
            System.out.printf("CALSTK $%s , value: %s \n", i, J8P.calstk[i] & 0xff);
        }
    }

    public static void printRegs() {
        System.out.println("Register Dump: ");
        byte[] regLookup = new byte[] {J8P.accumulator, J8P.indexes[0], J8P.indexes[1], J8P.indexes[2], J8P.indexes[3], J8P.indexes[4], J8P.indexes[5], J8P.indexes[6], J8P.indexes[7], J8P.mspptr, J8P.status};
        for (int i = 0; i < regLookup.length; i++) {
            System.out.printf("Register No. %s , value: %s \n", i, regLookup[i] & 0xff);
        }
    }
}

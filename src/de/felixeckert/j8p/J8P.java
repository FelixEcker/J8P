package de.felixeckert.j8p;

import java.util.Arrays;

public class J8P {
    // REGISTERS
    public static byte accumulator   = 0x0;     // ALU Operation Results, also the results of compare
    public static boolean numberMode = false;   // 1 = Negatives (-128 128); 0 = Positives (255)
    public static byte[] indexes = new byte[8]; // 8*1 byte registers
    public static byte mspptr = 0x0;            // Memory Space Pointer
    public static byte status = 0x0;            // CPU Status
    public static short pcounter = 0;           // Program Counter
    public static short plength  = 0;           // Program length

    // MEMORY
    public static byte[] program = new byte[64 * 1024];
    public static byte[] general = new byte[64 * 1024];

    // Steps the cpu once
    public static void step() {
        if (pcounter == 0 && !J8V.init) {
            J8V.init();
        }

        if (status == 0x7d) {
            System.err.println("Program error at Instruction: "+pcounter);
            reset();
        }

        System.out.println(pcounter);

        if (pcounter != 64 * 1024) {
            switch (program[pcounter]) {
                case 0x0:
                    stop();
                    break;
                case 0x1:
                    loadRegister(0);
                    break;
                case 0x2:
                    pushRegister(0);
                    break;
                // Utility Registers
                case 0x3:
                    loadRegister(1);
                    break;
                case 0x4:
                    pushRegister(1);
                    break;
                case 0x5:
                    loadRegister(2);
                    break;
                case 0x6:
                    pushRegister(2);
                    break;
                case 0x7:
                    loadRegister(3);
                    break;
                case 0x8:
                    pushRegister(3);
                    break;
                case 0x9:
                    loadRegister(4);
                    break;
                case 0xa:
                    pushRegister(4);
                    break;
                case 0xb:
                    loadRegister(5);
                    break;
                case 0xc:
                    pushRegister(5);
                    break;
                case 0xd:
                    loadRegister(6);
                    break;
                case 0xe:
                    pushRegister(6);
                    break;
                case 0xf:
                    loadRegister(7);
                    break;
                case 0x10:
                    pushRegister(7);
                    break;
                case 0x11:
                    loadRegister(8);
                    break;
                case 0x12:
                    pushRegister(8);
                    break;
                case 0x13:
                    loadRegister(10);
                    break;
                case 0x14:
                    pushRegister(10);
                    break;
                case 0x15:
                    loadRegister(9);
                    break;
                case 0x16:
                    pushRegister(9);
                    break;
                case 0x17:
                    numberMode = !numberMode;
                    pcounter++;
                    break;
                case 0x18:
                    add();
                    break;
                case 0x19:
                    sub();
                    break;
                case 0x1a:
                    div();
                    break;
                case 0x1b:
                    mul();
                    break;
                case 0x1c:
                    reset();
                    break;
                case 0x1d:
                    pushColor();
                    break;
                case 0x1e: // COMPARE
                    compare();
                    break;
                case 0x1f: // JE
                    if (accumulator == 1) {
                        pcounter = program[_program16BitAddress()];
                    } else {
                        pcounter += 3;
                    }
                    break;
                case 0x20: // JNE
                    if (accumulator == 0) {
                        pcounter = program[_program16BitAddress()];
                    } else {
                        pcounter += 3;
                    }
                    break;
                case 0x21: // JMP
                    pcounter = program[_program16BitAddress()];
                    break;
                case 63:
                    status = 0x7e;
                    break;
            }
        } else {
            // Program is finished, set cpu status so that the emulator can exit
            status = 0x7e;
        }
    }

    public static void reset() {
        accumulator   = 0x0;
        numberMode = false;
        indexes = new byte[8];
        mspptr = 0x0;
        status = 0x0;
        pcounter = 0;
        plength  = 0;
    }

    // INSTRUCTIONS
    public static void stop() {
        // Set cpu status to 0xff, which tells the emulator to stop execution of current program and stand by
        status = 0x7f;
    }

    // Compares 2 values from specified memory addresses.
    // The compare result is the loaded on to the accumulator
    // in which 1 represents equal and 0 not equal
    public static void compare() {
        byte[] vs = _programBytePair();
        if (vs[0] == vs[1]) {
            _loadRegister(0, (byte) 1);
        } else {
            _loadRegister(0, (byte) 0);
        }

        pcounter += 7;
    }

    // Pushes 3 Values to 3 Adresses in memory, with only one explicitly specified adress
    // The other adresses are found by incrementing the base adress by 1 and 2.
    // Intended for easily pushing RGB colors for a pixel in the Video RAM.
    public static void pushColor() {
        int pixel = _program16BitAddress();

        _pushRegister(1, (pixel & 0xFFFFFF));
        _pushRegister(2, ((pixel + 1) & 0xFFFFFF));
        _pushRegister(3, ((pixel + 2) & 0xFFFFFF));

        pcounter += 3;
    }

    // Register Functions
    ////////////////////////////
    // Register IDs:
    // 0 = Accumulator
    // 1 = Utility 1
    // 2 = Utility 2
    // 3 = Utility 3
    // 4 = Utility 4
    // 5 = Utility 5
    // 6 = Utility 6
    // 7 = Utility 7
    // 8 = Utility 8
    // 9 = Memory-Space Pointer
    // 10 = Processor Status

    // Loads a register. The actual loading is achieved through the internal function
    // "J8P#_loadRegister(int register, byte value)"
    public static void loadRegister(int register) {
        _loadRegister(register, _programBytePair()[0]);

        pcounter += 2;
    }

    // Pushes a Register to a specific place in Memory
    // Uses internal function "J8P#_pushRegister(int register, short address)"
    public static void pushRegister(int register) {
        _pushRegister(register, _program16BitAddress());
        pcounter += 2;
    }

    // These math instructions grab their values in the same way
    // the contents in the address "pcounter+1" of the program rom
    // specify the 1st address/value. It is followed by a byte which
    // is either 1 or 0. 0 Tells it that it is a value in memory
    // 1 tells it that the contents at "pcounter+1" are to be interpreted
    // as a number.
    // The same applies for the second value.
    // So the assembly code "ADD #$f #$e" (15+14) would look like this in binary
    // 18 f 1 e 1
    public static void add() {
        byte[] vs = _programBytePair();
        _loadRegister(0, (byte) (vs[0]+vs[1]));
    }

    public static void sub() {
        byte[] vs = _programBytePair();
        _loadRegister(0, (byte) (vs[0]-vs[1]));
    }

    public static void div() {
        byte[] vs = _programBytePair();

        System.out.println(Arrays.toString(vs));

        // Divide by 0 check.
        if (vs[1] == 0) {
            status = 0x7d;
            return;
        }

        _loadRegister(0, (byte) (vs[0]/vs[1]));
    }

    public static void mul() {
        byte[] vs = _programBytePair();
        _loadRegister(0, (byte) (vs[0]*vs[1]));
    }

    /////////////////////////////////////////////////////////////
    //                    Internal functions                   //
    /////////////////////////////////////////////////////////////

    // Returns a pair of byte from the program memory, relative to the
    // program counter. (byte 1 location = pcounter+1 & +2 ; byte 2 location = pcounter+4 & +5)
    public static byte[] _programBytePair() {
        // These booleans tell if the values at the memory space are address pointers or values.
        boolean address1 = program[pcounter+3] == 0;
        boolean address2 = program[pcounter+6] == 0;

        byte[] bytes = new byte[2];

        if (address1) {
            bytes[0] = Bus.operate((short) ((program[pcounter+1] << 8) | (program[pcounter+2] & 0xFF)), (byte)0x0, false, mspptr);
        } else {
            bytes[0] = program[pcounter+1];
        }

        if (address2) {
            bytes[1] = Bus.operate((short) ((program[pcounter+4] << 8) | (program[pcounter+5] & 0xFF)), (byte)0x0, false, mspptr);
        } else {
            bytes[1] = program[pcounter+4];
        }

        return bytes;
    }

    // Returns a 16 bit memory address relative to the program counter
    // (byte 1 = pcounter + 1, byte 2 = pcounter + 2)
    public static short _program16BitAddress() {
        return (short) ((program[pcounter+1] << 8) | (program[pcounter+2] & 0xFF));
    }

    // Internal function for loading a register
    private static void _loadRegister(int register, byte value) {
        switch (register) {
            case 0:
                accumulator = value;
                break;
            case 1:
                indexes[0] = value;
                break;
            case 2:
                indexes[1] = value;
                break;
            case 3:
                indexes[2] = value;
                break;
            case 4:
                indexes[3] = value;
                break;
            case 5:
                indexes[4] = value;
                break;
            case 6:
                indexes[5] = value;
                break;
            case 7:
                indexes[6] = value;
                break;
            case 8:
                indexes[7] = value;
                break;
            case 9:
                mspptr = value;
                break;
            case 10:
                status = value;
                break;
        }
    }

    // Internal function for loading a register.
    public static void _pushRegister(int register, int adress) {
        byte[] regLookup = new byte[] {accumulator, indexes[0], indexes[1], indexes[2], indexes[3], indexes[4], indexes[5], indexes[6], indexes[7], mspptr, status};
        Bus.operate(adress, regLookup[register], true, mspptr);
    }
}

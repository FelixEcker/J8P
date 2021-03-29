package de.felixeckert.j8p;

public class Bus {
    public static byte operate(int addr, byte value, boolean rw, byte memspace) {
        if (rw) {
            switch (memspace) {
                case 0x0:
                    J8P.general[addr & 0xFFFF] = value;
                    break;
                // Skip if any Program-ROM space is set
                case 0x1:
                    return 0x0;
                case 0x2:
                    if ((addr & 0xFFFFFF) > 64 * 1024) break;
                    J8V.memory[addr & 0xFFFF] = value;
                    break;
            }

            return 0x1;
        }

        switch (memspace) {
            case 0x0:
                return J8P.general[addr];
            case 0x1:
                return J8P.program[addr];
            case 0x9:
                if (addr > 20 * 1024) break;
                return J8V.memory[addr];
        }

        return 0x0;
    }
}

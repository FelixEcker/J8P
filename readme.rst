The J8P Digital Microprocessor
==============================
The J8P is a small hobby project. It is intended to be a reusable, single class
digital Microprocessor written in Java, which should only rely on standard libraries
if any.

Memory
------
The J8P could possibly take up use of up to 255 different banks of 64 Kilobytes Memory,
which could be combined to be ~16 Megabytes. These memory banks are addresses through
the "Bus". Which is a helper class for reading and writing memory.
The default distribution of memory is:
0 - General Memory (Processor-Class Internal)
1 - Program Memory
2 - Video Memory (J8V)
The current memory bank is held in a dedicated cpu-register.

The Call Stack
--------------
The call stack is  the biggest register of the CPU, it can hold up to 2 kilo bytes of data
and is mostly used for storing pointers to call origins of subroutines and for return codes
of those.
It comes with 3 Base Operations:
    - Push
    - Pop
    - Peek

The push operation simply pushes a value onto the top of the stack, in case it is filled, it will
clear the stack and write to the first point.
The Pop operation simply deletes the top value of the stack.
The Peek operation saves the top value of the stack in a specified place in memory.

Registers
---------

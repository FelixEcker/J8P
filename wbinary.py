bytes = [
    0x25, 0x22, 0x22,      # SPUS $#2222
    0x25, 0x22, 0x22,      # SPUS $#2222
    0x25, 0x22, 0xfa,      # SPUS $#22fa
    0x25, 0x22, 0x22,      # SPUS $#2222
    0x24,                  # SPOP
    0x26, 0x00, 0x00,      # SPEE $#0000
    0x1,  0x00, 0x00, 0x0, # LDA $0000
    0x3,  0x00, 0x01, 0x0  # LDS $1
    ]
file  = open("wbinary.bin", "wb")
print ("Written ", file.write(bytearray(bytes)), " bytes")
file.close()
/**
 * RISC-V Instruction Set Simulator
 * <p>
 * A tiny first step to get the simulator started.
 *
 * @author Martin Schoeberl (martin@jopdesign.com)
 */
public class IsaSim {

    static int pc;
    static int reg[] = new int[32];
    static byte[] memory = new byte[1 << 20]; // 1 MB of memory

    // Here the first program hard coded as an array
    static int progr[] = {
            // As minimal RISC-V assembler example
            0x00200093, // addi x1 x0 2
            0x00300113, // addi x2 x0 3
            0x002081b3, // add x3 x1 x2
            0x402081b3, // sub x3 x1 x2
            0x002091b3, // sll x3 x1 x2
            0x0020d1b3, // srl x3 x1 x2
            0xff000193, // addi x3 x0 -16
            0x4011d1b3, // sra x3 x3 x1
            0x0020a1b3, // slt x3 x1 x2
            0x0020b1b3, // sltu x3 x1 x2
            0x00309193, // slli x3 x1 3
            0x0050a193, // slti x3 x1 5
            0x0050b193, // sltiu x3 x1 5
            0x0030d193, // srli x3 x1 3
            0xff000193, // addi x3 x0 -16
            0x4021d193, // srai x3 x3 2
            0x123451b7, // lui x3 0x12345
            0x00001197, // auipc x3 1
    };

    public static void main(String[] args) {

        System.out.println("Hello RISC-V World!");

        pc = 0;

        for (; ; ) {

            //progr is an array containg the instructions//
            //pc >>2 is the same as pc/4, knows which instruction to use based on pc count in the array//

            int instr = progr[pc >> 2];
            int opcode = instr & 0x7f;//extracts the opcode //
            int rd = (instr >> 7) & 0x01f;//destination register//
            int funct3 = (instr >> 12) & 0x7;//sub opcode
            int rs1 = (instr >> 15) & 0x01f;//source register//
            int rs2 = (instr >> 20) & 0x1f;//other source register//
            int imm = (instr >> 20); //the imm5//
            int funct7 = (instr >> 25) & 0x7f;// sub opcode
            int immS = ((instr >> 25) << 5) | ((instr >> 7) & 0x1f); // used for store functions
            int branchOffset =
        ((instr >> 31) << 12) |
        (((instr >> 7) & 0x1) << 11) |
        (((instr >> 25) & 0x3f) << 5) |
        (((instr >> 8) & 0xf) << 1);

            switch (opcode) {

                case 0x13: // immediate operations
                    if (funct3 == 0x0) {
                        // ADDI
                        reg[rd] = reg[rs1] + imm;

                    } else if (funct3 == 0x4) {
                        // XORI
                        reg[rd] = reg[rs1] & imm;

                    } else if (funct3 == 0x7) {
                        // ANDI
                        reg[rd] = reg[rs1] & imm;

                    } else if (funct3 == 0x6) {
                        // ORI
                        reg[rd] = reg[rs1] | reg[rs2];

                    } else if (funct3 == 0x1) {
                        // SLLI
                        reg[rd] = reg[rs1] << (imm & 0x1f);

                    } else if (funct3 == 0x2) {
                        // SLTI
                        reg[rd] = reg[rs1] < imm ? 1 : 0;

                    } else if (funct3 == 0x3) {
                        // SLTIU
                        reg[rd] = Integer.compareUnsigned(reg[rs1], imm) < 0 ? 1 : 0;

                    } else if (funct3 == 0x5) {
                        if (funct7 == 0x00) {
                            // SRLI - logical
                            reg[rd] = reg[rs1] >>> (imm & 0x1f);
                        } else if (funct7 == 0x20) {
                            // SRAI - arithmetic
                            reg[rd] = reg[rs1] >> (imm & 0x1f);
                        }
                    }
                    break;

                case 0x33: // arithmetic operations

                    if (funct3 == 0x0 && funct7 == 0x00) {
                        // ADD
                        reg[rd] = reg[rs1] + reg[rs2];

                    } else if (funct3 == 0x0 && funct7 == 0x20) {
                        // SUB
                        reg[rd] = reg[rs1] - reg[rs2];

                    } else if (funct3 == 0x4 && funct7 == 0x00) {
                        // XOR
                        reg[rd] = reg[rs1] ^ reg[rs2];

                    } else if (funct3 == 0x6 && funct7 == 0x00) {
                        // OR
                        reg[rd] = reg[rs1] | reg[rs2];

                    } else if (funct3 == 0x7 && funct7 == 0x00) {
                        // AND
                        reg[rd] = reg[rs1] & reg[rs2];

                    } else if (funct3 == 0x1 && funct7 == 0x00) {
                        // SLL
                        reg[rd] = reg[rs1] << (reg[rs2] & 0x1f);

                    } else if (funct3 == 0x2 && funct7 == 0x00) {
                        // SLT - signed comparison
                        reg[rd] = reg[rs1] < reg[rs2] ? 1 : 0;

                    } else if (funct3 == 0x3 && funct7 == 0x00) {
                        // SLTU - unsigned comparison
                        reg[rd] = Integer.compareUnsigned(reg[rs1], reg[rs2]) < 0 ? 1 : 0;

                    } else if (funct3 == 0x5 && funct7 == 0x00) {
                        // SRL
                        reg[rd] = reg[rs1] >>> (reg[rs2] & 0x1f);

                    } else if (funct3 == 0x5 && funct7 == 0x20) {
                        // SRA
                        reg[rd] = reg[rs1] >> (reg[rs2] & 0x1f);
                    }
                    break;

                case 0x03: { //load operations
                    int load_address = imm + rs1;

                    switch (funct3) {
                        case 0: //lb
                            reg[rd] = memory[load_address];
                            break;

                        case 1: //lh
                            reg[rd] = (memory[load_address] & 0xff)
                                    | (memory[load_address + 1] << 8);
                            break;

                        case 2: //lw
                            reg[rd] = (memory[load_address] & 0xff)
                                    | ((memory[load_address + 1] & 0xff) << 8)
                                    | ((memory[load_address + 2] & 0xff) << 16)
                                    | (memory[load_address + 3] << 24);
                            break;

                        case 3: //doesn't exist
                            throw new IllegalStateException("not valid load function");

                        case 4: //lbu
                            reg[rd] = memory[load_address] & 0xff;
                            break;

                        case 5: //lhu
                            reg[rd] = (memory[load_address] & 0xff)
                                    | ((memory[load_address + 1] & 0xff) << 8);
                            break;
                    }
                    break;
                }

                case 0x23: { //store functions
                    int store_address = reg[rs1] + immS;

                    switch (funct3) {
                        case 0: //sb
                            memory[store_address] = (byte) reg[rs2];
                            break;

                        case 1: //sh
                            memory[store_address] = (byte) (reg[rs2]);
                            memory[store_address + 1] = (byte) (reg[rs2] >> 8);
                            break;

                        case 2: //sw
                            memory[store_address] = (byte) reg[rs2];
                            memory[store_address + 1] = (byte) (reg[rs2] >> 8);
                            memory[store_address + 2] = (byte) (reg[rs2] >> 16);
                            memory[store_address + 3] = (byte) (reg[rs2] >> 24);
                            break;
                    }
                    break;
                }

                case 0x37: // LUI
                    reg[rd] = instr & 0xfffff000;
                    break;

                case 0x17: // AIUPC
                    reg[rd] = pc + (instr & 0xfffff000);
                    break;
                    

                
                

                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }

            pc += 4; // One instruction is four bytes

            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }

            if ((pc >> 2) >= progr.length) {
                break;
            }

            System.out.println();
        }

        System.out.println();
        System.out.println("Program exit");
    }
}
/**
 * RISC-V Instruction Set Simulator
 * <p>
 * A tiny first step to get the simulator started. Can execute just a single
 * RISC-V instruction.
 *
 * @author Martin Schoeberl (martin@jopdesign.com)
 */
public class IsaSim {

	static int pc;
	static int reg[] = new int[32];

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

			switch (opcode) {

                case 0x03 //load operations
                     int load_address = imm + rs1;
                    switch (funct3) {
                    case 0:
                        reg[rd] = memory[load_address]; //lb
                            break;
                    case 1:

                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        reg[rd] = memory[load_address] & 0xff; //lbu
                        break;
                    }
                    break;
				case 0x13://0x13 is addi//
					switch (funct3) {
						case 0:
							reg[rd] = reg[rs1] + imm;
							break;
						case 1: // SLLI
							reg[rd] = reg[rs1] << (imm & 0x1f);
							break;
						case 2: // SLTI
							reg[rd] = reg[rs1] < imm ? 1 : 0;
							break;
						case 3: // SLTIU
							reg[rd] = Integer.compareUnsigned(reg[rs1], imm) < 0 ? 1 : 0;
							break;
						case 5:
							if (funct7 == 0x00) { // SRLI - logical
								reg[rd] = reg[rs1] >>> (imm & 0x1f);
							} else if (funct7 == 0x20) { // SRAI - arithmetic
								reg[rd] = reg[rs1] >> (imm & 0x1f);
							}
							break;
					}
					break;
				case 0x33: //arithmetic operations
					switch (funct3) {
						case 0:
							if (funct7 == 0x20) { //sub
								reg[rd] = reg[rs1] - reg[rs2];
							} else {              //add
								reg[rd] = reg[rs1] + reg[rs2];
							}
							break;
						case 1:
							if (funct7 == 0x0) { // SLL
								reg[rd] = reg[rs1] << (reg[rs2] & 0x1f);
							}
							break;
						case 2:
							if (funct7 == 0x0) { // SLT - signed comparison
								reg[rd] = reg[rs1] < reg[rs2] ? 1 : 0;
							}
							break;
						case 3:
							if (funct7 == 0x0) { // SLTU - unsigned comparison
								reg[rd] = Integer.compareUnsigned(reg[rs1], reg[rs2]) < 0 ? 1 : 0;
							}
							break;
						case 5:
							if (funct7 == 0x0) { // SRL
								reg[rd] = reg[rs1] >>> (reg[rs2] & 0x1f);
							} else if (funct7 == 0x20) { // SRA
								reg[rd] = reg[rs1] >> (reg[rs2] & 0x1f);
							}
							break;
					}
					break;
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

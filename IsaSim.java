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
    static int reg[] = new int[4];

    // Here the first program hard coded as an array
    static int progr[] = {
            // As minimal RISC-V assembler example
            0x00200093, // addi x1 x0 2
            0x00300113, // addi x2 x0 3
            0x002081b3, // add x3 x1 x2
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
            int funct3 = (instr >> 12) & 0x7f;//sub opcode
            int rs1 = (instr >> 15) & 0x01f;//source register//
            int rs2 = (instr >> 20) & 0x1f;//other source register//
            int imm = (instr >> 20); //the imm5//
            int funct7 = (instr >> 25) & 0x7f;// sub opcode

            switch (opcode) {

                case 0x13://0x13 is addi//
                    reg[rd] = reg[rs1] + imm;
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
                    }
                    break;
                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }

            pc += 4; // One instruction is four bytes
            if ((pc >> 2) >= progr.length) {
                break;
            }
            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            System.out.println();
        }

        System.out.println("Program exit");

    }

}

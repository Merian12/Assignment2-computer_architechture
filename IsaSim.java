
/**
 * RISC-V Instruction Set Simulator
 * <p>
 * A tiny first step to get the simulator started.
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
            0x0020C1B3, // xor x3, x1, x2
            0x0020e1b3, //or x3, x1,x2
            0x0020F1B3, // and x3, x1, x2
            0x0030C193, // xori x3, x1, 3
            0x0030F193, // andi x3, x1, 3
            0x0060E193, // ori x3, x1, 6
        
    };

    public static void main(String[] args) {

        System.out.println("Hello RISC-V World!");

        pc = 0;

        for (;;) {

            // progr is an array containing the instructions
            // pc >> 2 is the same as pc / 4

            int instr = progr[pc >> 2];
            int opcode = instr & 0x7f; // extracts the opcode
            int rd = (instr >> 7) & 0x01f; // destination register
            int rs1 = (instr >> 15) & 0x01f; // source register
            int rs2 = (instr >> 20) & 0x1f; // other source register
            int imm = instr >> 20; // immediate
            int funct3 = (instr >> 12) & 0x7;//specifies exact instruction//
            int funct7 = (instr >> 25) & 0x7f;

            switch (opcode) {

                case 0x13: if(funct3 == 0x0){
                // ADDI
                    reg[rd] = reg[rs1] + imm;}
                    else if (funct3 == 0x4){
                    //XORI
                    reg[rd] = reg[rs1] & imm;
                    }else if(funct3 ==0x7){
                    //ANDI
                    reg[rd]=reg[rs1]& imm;

                    //ORI

                    }else if(funct3==0x6){
                        reg[rd]=reg[rs1]|reg[rs2];
                    }
                    break;

                case 0x33: 

                    if (funct3 == 0x0 && funct7 == 0x00) {
                        // ADD
                        reg[rd] = reg[rs1] + reg[rs2];

                    } else if (funct3 == 0x4 && funct7 == 0x00) {
                        // XOR
                        reg[rd] = reg[rs1] ^ reg[rs2];

                    }else if(funct3 ==0x6 && funct7==0x00){
                        //OR
                        reg[rd]=reg[rs1]|reg[rs2];

                    }else if(funct3 ==0x7 && funct7==0x00){
                        //AND
                        reg[rd]=reg[rs1]&reg[rs2];
                    }
                        

                    

                    break;

                default:
                    System.out.println("Opcode " + opcode + " not implemented");
                    break;
            }

            pc += 4;

            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            System.out.println();

            if ((pc >> 2) >= progr.length) {
                break;
            }
        }
    }
}


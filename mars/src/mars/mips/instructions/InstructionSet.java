   package mars.mips.instructions;
   import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;

import java.util.*;
import java.io.*;
	
	/*
Copyright (c) 2003-2013,  Pete Sanderson and Kenneth Vollmar

Developed by Pete Sanderson (psanderson@otterbein.edu)
and Kenneth Vollmar (kenvollmar@missouristate.edu)

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the 
"Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, 
distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be 
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

(MIT license, http://www.opensource.org/licenses/mit-license.html)
 */

/**
 * The list of Instruction objects, each of which represents a MIPS instruction.
 * The instruction may either be basic (translates into binary machine code) or
 * extended (translates into sequence of one or more basic instructions).
 *
 * @author Pete Sanderson and Ken Vollmar
 * @version August 2003-5
 */

    public class InstructionSet
   {
      private ArrayList<Instruction> instructionList;
	  private ArrayList opcodeMatchMaps;
      private SyscallLoader syscallLoader;
    /**
     * Creates a new InstructionSet object.
     */
       public InstructionSet()
      {
         instructionList = new ArrayList();
      
      }
    /**
     * Retrieve the current instruction set.
     */
       public ArrayList getInstructionList()
      {
         return instructionList;
      
      }
    /**
     * Adds all instructions to the set.  A given extended instruction may have
     * more than one Instruction object, depending on how many formats it can have.
     * @see Instruction
     * @see BasicInstruction
     * @see ExtendedInstruction
     */
       public void populate()
      {
        /* Here is where the parade begins.  Every instruction is added to the set here.*/
      
        // ////////////////////////////////////   BASIC INSTRUCTIONS START HERE ////////////////////////////////
      
           instructionList.add(
                   new BasicInstruction("nop",
               	 "Null operation : machine code is all zeroes",
                   BasicInstructionFormat.R_FORMAT,
                   "000000 00000 00000 00000 00000 000000",
                   new SimulationCode()
                  {
                      public void simulate(ProgramStatement statement) throws ProcessingException
                     {
                     	// Hey I like this so far!
                     }
                  }));
           
            instructionList.add(
                   new BasicInstruction("add R1,R2,R3",
               	 "Addition: set R1 to (R2 plus R3)",
                   BasicInstructionFormat.R_FORMAT,
                   "000000 sssss ttttt fffff 00000 000000",
                   new SimulationCode()
                  {
                      public void simulate(ProgramStatement statement) throws ProcessingException
                     {
                        int[] operands = statement.getOperands();
                        int add1 = RegisterFile.getValue(operands[1]);
                        int add2 = RegisterFile.getValue(operands[2]);
                        int sum = add1 + add2;
                     // overflow on A+B detected when A and B have same sign and A+B has other sign.
                        if ((add1 >= 0 && add2 >= 0 && sum < 0)
                           || (add1 < 0 && add2 < 0 && sum >= 0))
                        {
                        	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                        }else{
                        	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                     }
                  }));
            instructionList.add(
                    new BasicInstruction("addc R1,R2,R3",
                	 "Addition: set R1 to (R2 plus R3) with Carry",
                    BasicInstructionFormat.R_FORMAT,
                    "000010 sssss ttttt fffff 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int add1 = RegisterFile.getValue(operands[1]);
                         int add2 = RegisterFile.getValue(operands[2]);
                         int carry = RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & (1 << 29);
                         int sum = add1 + add2 + carry;
                      // overflow on A+B detected when A and B have same sign and A+B has other sign.
                         if ((add1 >= 0 && add2 >= 0 && sum < 0)
                            || (add1 < 0 && add2 < 0 && sum >= 0))
                         {
                         	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                         }else{
                         	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                         }
                         RegisterFile.updateRegister(operands[0], sum);
                      }
                   }));
            instructionList.add(
                    new BasicInstruction("addk R1,R2,R3",
                	 "Addition: set R1 to (R2 plus R3) and Keep Carry",
                    BasicInstructionFormat.R_FORMAT,
                    "000100 sssss ttttt fffff 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int add1 = RegisterFile.getValue(operands[1]);
                         int add2 = RegisterFile.getValue(operands[2]);
                         int sum = add1 + add2;
                         RegisterFile.updateRegister(operands[0], sum);
                      }
                   }));
            instructionList.add(
                    new BasicInstruction("addkc R1,R2,R3",
                	 "Addition: set R1 to (R2 plus R3) with Carry and Keep Carry",
                    BasicInstructionFormat.R_FORMAT,
                    "000110 sssss ttttt fffff 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int add1 = RegisterFile.getValue(operands[1]);
                         int add2 = RegisterFile.getValue(operands[2]);
                         int carry = RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & (1 << 29);
                         int sum = add1 + add2 + carry;
                         RegisterFile.updateRegister(operands[0], sum);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("addi R1,R2,-100",
                	 "Addition immediate with overflow : set R1 to (R2 plus signed 16-bit immediate)",
                    BasicInstructionFormat.I_FORMAT,
                    "001000 sssss fffff tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int add1 = RegisterFile.getValue(operands[1]);
                         int add2 = sext(operands[2]);
                         int sum = add1 + add2;
                      // overflow on A+B detected when A and B have same sign and A+B has other sign.
                         if ((add1 >= 0 && add2 >= 0 && sum < 0)
                            || (add1 < 0 && add2 < 0 && sum >= 0))
                         {
                         	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                         }else{
                          	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                          }
                         RegisterFile.updateRegister(operands[0], sum);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("addic R1,R2,-100",
                	 "Addition immediate with overflow : set R1 to (R2 plus signed 16-bit immediate)",
                    BasicInstructionFormat.I_FORMAT,
                    "001010 sssss fffff tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int add1 = RegisterFile.getValue(operands[1]);
                         int add2 = sext(operands[2]);
                         int carry = RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & (1 << 29);
                         int sum = add1 + add2 + carry;
                      // overflow on A+B detected when A and B have same sign and A+B has other sign.
                         if ((add1 >= 0 && add2 >= 0 && sum < 0)
                            || (add1 < 0 && add2 < 0 && sum >= 0))
                         {
                         	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                         }else{
                          	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                          }
                         RegisterFile.updateRegister(operands[0], sum);
                      }
                   }));
            instructionList.add(
                    new BasicInstruction("addik R1,R2,-100",
                	 "Addition immediate with overflow : set R1 to (R2 plus signed 16-bit immediate)",
                    BasicInstructionFormat.I_FORMAT,
                    "001100 sssss fffff tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int add1 = RegisterFile.getValue(operands[1]);
                         int add2 = sext(operands[2]);
                         int sum = add1 + add2;
                         RegisterFile.updateRegister(operands[0], sum);
                      }
                   }));
            instructionList.add(
                    new BasicInstruction("addikc R1,R2,-100",
                	 "Addition immediate with overflow : set R1 to (R2 plus signed 16-bit immediate)",
                    BasicInstructionFormat.I_FORMAT,
                    "001110 sssss fffff tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int add1 = RegisterFile.getValue(operands[1]);
                         int add2 = sext(operands[2]);
                         int carry = RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & (1 << 29);
                         int sum = add1 + add2 + carry;
                         RegisterFile.updateRegister(operands[0], sum);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("and R1,R2,R3",
                	 "Bitwise AND : Set R1 to bitwise AND of R2 and R3",
                    BasicInstructionFormat.R_FORMAT,
                    "100001 sssss ttttt fffff 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0],
                            RegisterFile.getValue(operands[1])
                            & RegisterFile.getValue(operands[2]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("andi R1,R2,-100",
                	 "Bitwise AND immediate : Set R1 to bitwise AND of R2 and sign-extended 16-bit immediate",
                    BasicInstructionFormat.I_FORMAT,
                    "101001 sssss fffff tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0],
                            RegisterFile.getValue(operands[1])
                            & sext(operands[2]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("andn R1,R2,R3",
                	 "Bitwise AND : Set R1 to bitwise AND of R2 and NOT R3",
                    BasicInstructionFormat.R_FORMAT,
                    "100011 sssss ttttt fffff 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0],
                            RegisterFile.getValue(operands[1])
                            & ~RegisterFile.getValue(operands[2]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("andni R1,R2,-100",
                	 "Bitwise AND immediate : Set R1 to bitwise AND of R2 and NOT sign-extended 16-bit immediate",
                    BasicInstructionFormat.I_FORMAT,
                    "101011 sssss fffff tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0],
                            RegisterFile.getValue(operands[1])
                            & ~sext(operands[2]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("beq R1,R2",
                    "Branch if equal : Branch to statement at adress of R2 if R1 is equal zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 00000 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) == 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("beqd R1,R2",
                    "Branch with delay if equal : Branch to statement at adress of R2 if R1 is equal zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 10000 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) == 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("beqi R1,-100",
                	 "Branch if equal : Branch to statement at adress of signed 16-bit immediate if R1 is equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00000 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) == 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("beqi R1,label",
                	 "Branch if equal : Branch to statement at adress of label if R1 is equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00000 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) == 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("beqid R1,-100",
                	 "Branch with delay if equal : Branch to statement at adress of signed 16-bit immediate if R1 is equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10000 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) == 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("beqid R1,label",
                	 "Branch with delay if equal : Branch to statement at adress of label if R1 is equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10000 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) == 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bge R1,R2",
                    "Branch if greater or equal : Branch to statement at adress of R2 if R1 is greater or equal zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 00101 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) >= 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bged R1,R2",
                    "Branch with delay if greater or equal : Branch to statement at adress of R2 if R1 is greater or equal zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 10101 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) >= 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgei R1,-100",
                	 "Branch if greater or equal : Branch to statement at adress of signed 16-bit immediate if R1 is greater or equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00101 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) >= 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgei R1,label",
                	 "Branch if greater or equal : Branch to statement at adress of label if R1 is greater or equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00101 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) >= 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgeid R1,-100",
                	 "Branch with delay if greater or equal : Branch to statement at adress of signed 16-bit immediate if R1 is greater or equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10101 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) >= 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgeid R1,label",
                	 "Branch with delay if greater or equal : Branch to statement at adress of label if R1 is greater or equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10101 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) >= 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgt R1,R2",
                    "Branch if greater : Branch to statement at adress of R2 if R1 is greater zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 00100 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) > 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgtd R1,R2",
                    "Branch with delay if greater : Branch to statement at adress of R2 if R1 is greater zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 10100 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) > 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgti R1,-100",
                	 "Branch if greater : Branch to statement at adress of signed 16-bit immediate if R1 is greater zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00100 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) > 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgti R1,label",
                	 "Branch if greater : Branch to statement at adress of signed 16-bit immediate if R1 is greater zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00100 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) > 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgtid R1,-100",
                	 "Branch with delay if greater : Branch to statement at adress of signed 16-bit immediate if R1 is greater zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10100 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) > 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bgtid R1,label",
                	 "Branch with delay if greater : Branch to statement at adress of label if R1 is greater zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10100 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) > 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("ble R1,R2",
                    "Branch if less or equal : Branch to statement at adress of R2 if R1 is less or equal zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 00011 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) <= 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bled R1,R2",
                    "Branch with delay if less or equal : Branch to statement at adress of R2 if R1 is less or equal zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 10011 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) <= 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("blei R1,-100",
                	 "Branch if less or equal : Branch to statement at adress of signed 16-bit immediate if R1 is less or equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00011 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) <= 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("blei R1,label",
                	 "Branch if less or equal : Branch to statement at adress of signed 16-bit immediate if R1 is less or equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00011 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) <= 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bleid R1,-100",
                	 "Branch with delay if less or equal : Branch to statement at adress of signed 16-bit immediate if R1 is less or equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10011 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) <= 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bleid R1,label",
                	 "Branch with delay if less or equal : Branch to statement at adress of label if R1 is less or equal zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10011 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) <= 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("blt R1,R2",
                    "Branch if less than : Branch to statement at adress of R2 if R1 is less than zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 00010 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) < 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bltd R1,R2",
                    "Branch with delay if less than : Branch to statement at adress of R2 if R1 is less than zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 10010 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) < 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("blti R1,-100",
                	 "Branch if less than : Branch to statement at adress of signed 16-bit immediate if R1 is less than zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00010 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) < 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("blti R1,label",
                	 "Branch if less than : Branch to statement at adress of label if R1 is less than zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00010 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) < 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            
            instructionList.add(
                    new BasicInstruction("bltid R1,-100",
                	 "Branch with delay if less than : Branch to statement at adress of signed 16-bit immediate if R1 is less than zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10010 fffff ssssssssssssssss",
                    new SimulationCode()
                    {
                        public void simulate(ProgramStatement statement) throws ProcessingException
                       {
                          int[] operands = statement.getOperands();
                          Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                          if (RegisterFile.getValue(operands[0]) < 0)
                          {
                             processBranch(sext(operands[1]));
                          }
                       }
                    }));
            
            instructionList.add(
                    new BasicInstruction("bltid R1,label",
                	 "Branch with delay if less than : Branch to statement at adress of label if R1 is less than zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10010 fffff ssssssssssssssss",
                    new SimulationCode()
                    {
                        public void simulate(ProgramStatement statement) throws ProcessingException
                       {
                          int[] operands = statement.getOperands();
                          Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                          if (RegisterFile.getValue(operands[0]) < 0)
                          {
                             processJump(operands[1]);
                          }
                       }
                    }));
            
            instructionList.add(
                    new BasicInstruction("bne R1,R2",
                    "Branch if not equal : Branch to statement at adress of R2 if R1 is not zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 00001 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) != 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bned R1,R2",
                    "Branch with delay if not equal : Branch to statement at adress of R2 if R1 is not zero",
                	 BasicInstructionFormat.R_FORMAT,
                    "100111 10001 fffff sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         if (RegisterFile.getValue(operands[0]) != 0)
                         {
                            processBranch(RegisterFile.getValue(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bnei R1,-100",
                	 "Branch if not equal : Branch to statement at adress of signed 16-bit immediate if R1 is not zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00001 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) != 0)
                         {
                            processBranch(sext(operands[1]));
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bnei R1,label",
                	 "Branch if not equal : Branch to statement at adress of label if R1 is not zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 00001 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         if (RegisterFile.getValue(operands[0]) != 0)
                         {
                            processJump(operands[1]);
                         }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bneid R1,-100",
                	 "Branch with delay if not equal : Branch to statement at adress of signed 16-bit immediate if R1 is not zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10001 fffff ssssssssssssssss",
                    new SimulationCode()
                    {
                        public void simulate(ProgramStatement statement) throws ProcessingException
                       {
                          int[] operands = statement.getOperands();
                          Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                          if (RegisterFile.getValue(operands[0]) != 0)
                          {
                             processBranch(sext(operands[1]));
                          }
                       }
                    }));
            
            instructionList.add(
                    new BasicInstruction("bneid R1,label",
                	 "Branch with delay if not equal : Branch to statement at adress of label if R1 is not zero",
                    BasicInstructionFormat.I_FORMAT,
                    "101111 10001 fffff ssssssssssssssss",
                    new SimulationCode()
                    {
                        public void simulate(ProgramStatement statement) throws ProcessingException
                       {
                          int[] operands = statement.getOperands();
                          Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                          if (RegisterFile.getValue(operands[0]) != 0)
                          {
                             processJump(operands[1]);
                          }
                       }
                    }));
            
            instructionList.add(
                    new BasicInstruction("br R1",
                    "Unconditional Branch : Branch to statement at adress of R1",
                	 BasicInstructionFormat.R_FORMAT,
                    "100110 00000 00000 fffff 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         processBranch(RegisterFile.getValue(operands[0]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bra R1",
                    "Unconditional absolute Branch : Jump to statement at adress of R1",
                	 BasicInstructionFormat.R_FORMAT,
                    "100110 00000 01000 fffff 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         processJump(RegisterFile.getValue(operands[0]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brd R1",
                    "Unconditional Branch with delay : Branch to statement at adress of R1",
                	 BasicInstructionFormat.R_FORMAT,
                    "100110 00000 10000 fffff 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processBranch(RegisterFile.getValue(operands[0]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brad R1",
                    "Unconditional absolute Branch with delay : Jump to statement at adress of R1",
                	 BasicInstructionFormat.R_FORMAT,
                    "100110 00000 11000 fffff 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processJump(RegisterFile.getValue(operands[0]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brld R1,R2",
                    "Unconditional Branch with link and delay : Branch to statement at adress of R2 and store PC in R1",
                	 BasicInstructionFormat.R_FORMAT,
                    "100110 fffff 10100 sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processReturnAddress(operands[0]);//RegisterFile.updateRegister(operands[0], RegisterFile.getProgramCounter());
                         processBranch(RegisterFile.getValue(operands[1]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brald R1,R2",
                    "Unconditional absolute Branch with link and delay : Jump to statement at adress of R2 and store PC in R1",
                	 BasicInstructionFormat.R_FORMAT,
                    "100110 fffff 11100 sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processReturnAddress(operands[0]);//RegisterFile.updateRegister(operands[0], RegisterFile.getProgramCounter());
                         processJump(RegisterFile.getValue(operands[1]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("bri -100",
                    "Unconditional Branch : Branch to statement at adress of signed 16-bit immediate",
                	 BasicInstructionFormat.I_FORMAT,
                    "101110 00000 00000 ffffffffffffffff",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         processBranch(sext(operands[0]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brai -100",
                    "Unconditional absolute Branch : Jump to statement at adress of signed 16-bit immediate",
                	 BasicInstructionFormat.I_FORMAT,
                    "101110 00000 01000 ffffffffffffffff",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         processJump(sext(operands[0]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brai label",
                    "Unconditional absolute Branch : Jump to statement at adress of label",
                	 BasicInstructionFormat.I_FORMAT,
                    "101110 00000 01000 ffffffffffffffff",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, false);
                         processJump(operands[0]);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brid -100",
                    "Unconditional Branch with delay : Branch to statement at adress of signed 16-bit immediate",
                	 BasicInstructionFormat.I_FORMAT,
                    "101110 00000 10000 ffffffffffffffff",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processBranch(sext(operands[0]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("braid -100",
                    "Unconditional absolute Branch with delay : Jump to statement at adress of signed 16-bit immediate",
                	 BasicInstructionFormat.I_FORMAT,
                    "101110 00000 11000 ffffffffffffffff",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processJump(sext(operands[0]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("braid label",
                    "Unconditional absolute Branch with delay : Jump to statement at adress of label",
                	 BasicInstructionFormat.I_FORMAT,
                    "101110 00000 11000 ffffffffffffffff",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processJump(operands[0]);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brlid R1,-100",
                    "Unconditional Branch with link and delay : Branch to statement at adress of signed 16-bit immediate and store PC in R1",
                	 BasicInstructionFormat.I_FORMAT,
                    "101110 fffff 10100 ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processReturnAddress(operands[0]);
                         processBranch(sext(operands[1]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brald R1,-100",
                    "Unconditional absolute Branch with link and delay : Jump to statement at adress of signed 16-bit immediate and store PC in R1",
                	 BasicInstructionFormat.I_FORMAT,
                    "101110 fffff 11100 ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processReturnAddress(operands[0]);
                         processJump(sext(operands[1]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brald R1,label",
                    "Unconditional absolute Branch with link and delay : Jump to statement at adress of label and store PC in R1",
                	 BasicInstructionFormat.I_FORMAT,
                    "101110 fffff 11100 ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processReturnAddress(operands[0]);
                         processJump(operands[1]);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brk R1,R2",
                    "Break : Branch and link to statement at adress of R2, store PC in R1 and set the BIP flag in MSR",
                	 BasicInstructionFormat.R_FORMAT,
                    "100110 fffff 01100 sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processReturnAddress(operands[0]);
                         processBranch(RegisterFile.getValue(operands[1]));
                         RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 28)));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("brki R1,-100",
                    "Break : Branch and link to statement at adress of signed 16-bit immediate, store PC in R1 and set the BIP flag in MSR",
                	 BasicInstructionFormat.R_FORMAT,
                    "100110 fffff 01100 sssss 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processReturnAddress(operands[0]);
                         processBranch(sext(operands[1]));
                         RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 28)));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("imm -100",
                    "Immediate : Store an immediate in a temporary register. The content of it will be added to the following Type-B instruction.",
                	 BasicInstructionFormat.I_FORMAT,
                    "101100 00000 00000 ffffffffffffffff",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(RegisterFile.IMMEDIATE_TEMP_REGISTER, operands[0]);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("lbu R1,R2,R3",
                	 "Load Byte Unsigned: Loads a byte from the memory location at (R2 plus R3) into R1",
                    BasicInstructionFormat.R_FORMAT,
                    "110000 fffff sssss ttttt 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                    	   int[] operands = statement.getOperands();
                           try
                           {
                              RegisterFile.updateRegister(operands[0],
                                  Globals.memory.getByte(
                                  RegisterFile.getValue(operands[1]
                                          + operands[2]))
                                                  << 24
                                                  >> 24);
                           } 
                               catch (AddressErrorException e)
                              {
                                 throw new ProcessingException(statement, e);
                              }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("lbui R1,R2,-100",
                	 "Load Byte Unsigned: Loads a byte from the memory location at (R2 plus sign extended 16bit immediate) into R1",
                    BasicInstructionFormat.I_FORMAT,
                    "111000 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                    	   int[] operands = statement.getOperands();
                           try
                           {
                              RegisterFile.updateRegister(operands[0],
                                  Globals.memory.getByte(
                                  RegisterFile.getValue(operands[1]
                                          + sext(operands[2])))
                                                  << 24
                                                  >> 24);
                           } 
                               catch (AddressErrorException e)
                              {
                                 throw new ProcessingException(statement, e);
                              }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("lhu R1,R2,R3",
                    "Load Halfword Unsigned : Loads a halfword from the memory location at (R2 plus R3) into R1",
                	 BasicInstructionFormat.R_FORMAT,
                    "110001 fffff sssss ttttt 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            RegisterFile.updateRegister(operands[0],
                                Globals.memory.getHalf(
                                RegisterFile.getValue(operands[2]
                                        + operands[1]))
                                                << 16
                                                >> 16);
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("lhui R1,R2,-100",
                    "Load Halfword Unsigned: Loads a halfword from the memory location at (R2 plus sign extended 16bit immediate) into R1",
                    BasicInstructionFormat.I_FORMAT,
                    "111001 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            RegisterFile.updateRegister(operands[0],
                                Globals.memory.getHalf(
                                RegisterFile.getValue(operands[2]
                                        + sext(operands[1])))
                                                << 16
                                                >> 16);
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("lw R1,R2,R3",
                    "Load Halfword Unsigned : Loads a word from the memory location at (R2 plus R3) into R1",
                	 BasicInstructionFormat.R_FORMAT,
                    "110010 fffff sssss ttttt 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            RegisterFile.updateRegister(operands[0],
                            //TODO: alignment (bit 30&31 = 0)
                                Globals.memory.getWord(
                                RegisterFile.getValue(operands[2]
                                        + operands[1])));
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("lwi R1,R2,-100",
                    "Load Halfword Unsigned: Loads a word from the memory location at (R2 plus sign extended 16bit immediate) into R1",
                    BasicInstructionFormat.I_FORMAT,
                    "111010 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            RegisterFile.updateRegister(operands[0],
                                Globals.memory.getWord(
                                RegisterFile.getValue(operands[2]
                                        + operands[1])));
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("mfs R1,R2",
                    "Move from Special Purpose Register : Move the content of the PC(0) or MSR(1) register to R1.",
                	 BasicInstructionFormat.R_FORMAT,
                    "100101 fffff 00000 100000000000000 s",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         if(operands[1]>0)
                        	 RegisterFile.updateRegister(operands[0], RegisterFile.getValue(35));
                         else
                        	 RegisterFile.updateRegister(operands[0], RegisterFile.getValue(32));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("mts R1,R2",
                    "Move To Special Purpose Register : Move the content of R2 to MSR(1) register",
                	 BasicInstructionFormat.R_FORMAT,
                    "100101 00000 sssss 110000000000000 f",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         if(operands[0]>0)
                        	 RegisterFile.updateRegister(35,RegisterFile.getValue(operands[1]));
                         else
                        	 throw new ProcessingException(statement, "PC cant be written");
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("mul R1,R2,R3",
                	 "Multiplication: Set R1 to low-order 32 bits of the product of R2 and R3",
                    BasicInstructionFormat.R_FORMAT,
                    "010000 fffff sssss ttttt 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         long product = (long) RegisterFile.getValue(operands[1])
                            * (long) RegisterFile.getValue(operands[2]);
                         RegisterFile.updateRegister(operands[0],
                            (int) ((product << 32) >> 32));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("muli R1,R2,-100",
                	 "Multiplication: Set R1 to low-order 32 bits of the product of R2 and R3",
                    BasicInstructionFormat.I_FORMAT,
                    "011000 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         long product = (long) RegisterFile.getValue(operands[1])
                            * (long) sext(operands[2]);
                         RegisterFile.updateRegister(operands[0],
                            (int) ((product << 32) >> 32));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("or R1,R2,R3",
                	 "Bitwise OR : Set R1 to bitwise OR of R2 and R3",
                    BasicInstructionFormat.R_FORMAT,
                    "100000 fffff sssss ttttt 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0],
                            RegisterFile.getValue(operands[1])
                            | RegisterFile.getValue(operands[2]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("ori R1,R2,-100",
                	 "Bitwise OR immediate : Set R1 to bitwise OR of R2 and sign-extended 16-bit immediate",
                    BasicInstructionFormat.I_FORMAT,
                    "101001 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0],
                            RegisterFile.getValue(operands[1])
                            | sext(operands[2]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rsub R1,R2,R3",
                	 "Subtraction : set R1 to (R3 minus R2)",
                    BasicInstructionFormat.R_FORMAT,
                    "000001 fffff sssss ttttt 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int sub2 = RegisterFile.getValue(operands[1]);
                         int sub1 = RegisterFile.getValue(operands[2]);
                         int dif = sub1 - sub2;
                      // overflow on A-B detected when A and B have opposite signs and A-B has B's sign
                         if ((sub1 >= 0 && sub2 < 0 && dif < 0)
                            || (sub1 < 0 && sub2 >= 0 && dif >= 0)
                            || (sub1 >= 0 && sub2 > 0 && dif < 0)
                            || (sub1 <= 0 && sub2 < 0 && dif > 0))
                         {
                        	 RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                         }else{
                          	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                          }
                         RegisterFile.updateRegister(operands[0], dif);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rsubc R1,R2,R3",
                	 "Subtraction with Carry: set R1 to (R3 minus R2) plus Carry",
                    BasicInstructionFormat.R_FORMAT,
                    "000011 fffff sssss ttttt 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int sub2 = RegisterFile.getValue(operands[1]);
                         int sub1 = RegisterFile.getValue(operands[2]);
                         int carry = RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & (1 << 29);
                         int dif = sub1 - sub2 + carry;
                      // overflow on A-B detected when A and B have opposite signs and A-B has B's sign
                         if ((sub1 >= 0 && sub2 < 0 && dif < 0)
                            || (sub1 < 0 && sub2 >= 0 && dif >= 0)
                            || (sub1 >= 0 && sub2 > 0 && dif < 0)
                            || (sub1 <= 0 && sub2 < 0 && dif > 0))
                         {
                        	 RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                         }else{
                          	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                          }
                         RegisterFile.updateRegister(operands[0], dif);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rsubk R1,R2,R3",
                	 "Subtraction with Keep: set R1 to (R3 minus R2) and keep carrybit",
                    BasicInstructionFormat.R_FORMAT,
                    "000101 fffff sssss ttttt 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int sub2 = RegisterFile.getValue(operands[1]);
                         int sub1 = RegisterFile.getValue(operands[2]);
                         int dif = sub1 - sub2;
                         RegisterFile.updateRegister(operands[0], dif);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rsubkc R1,R2,R3",
                	 "Subtraction with Keep and Carry: set R1 to (R3 minus R2) plus Carry and keep carrybit",
                    BasicInstructionFormat.R_FORMAT,
                    "000111 fffff sssss ttttt 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int sub2 = RegisterFile.getValue(operands[1]);
                         int sub1 = RegisterFile.getValue(operands[2]);
                         int carry = RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & (1 << 29);
                         int dif = sub1 - sub2 + carry;
                         RegisterFile.updateRegister(operands[0], dif);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rsubi R1,R2,-100",
                	 "Subtraction : set R1 to (immeditate 16bit sign extended minus R2)",
                    BasicInstructionFormat.I_FORMAT,
                    "001001 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int sub2 = RegisterFile.getValue(operands[1]);
                         int sub1 = sext(operands[2]);
                         int dif = sub1 - sub2;
                      // overflow on A-B detected when A and B have opposite signs and A-B has B's sign
                         if ((sub1 >= 0 && sub2 < 0 && dif < 0)
                            || (sub1 < 0 && sub2 >= 0 && dif >= 0)
                            || (sub1 >= 0 && sub2 > 0 && dif < 0)
                            || (sub1 <= 0 && sub2 < 0 && dif > 0))
                         {
                        	 RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                         }else{
                          	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                          }
                         RegisterFile.updateRegister(operands[0], dif);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rsubic R1,R2,-100",
                	 "Subtraction with Carry: set R1 to (immeditate 16bit sign extended minus R2) plus Carry",
                    BasicInstructionFormat.R_FORMAT,
                    "001011 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int sub2 = RegisterFile.getValue(operands[1]);
                         int sub1 = sext(operands[2]);
                         int carry = RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & (1 << 29);
                         int dif = sub1 - sub2 + carry;
                      // overflow on A-B detected when A and B have opposite signs and A-B has B's sign
                         if ((sub1 >= 0 && sub2 < 0 && dif < 0)
                            || (sub1 < 0 && sub2 >= 0 && dif >= 0)
                            || (sub1 >= 0 && sub2 > 0 && dif < 0)
                            || (sub1 <= 0 && sub2 < 0 && dif > 0))
                         {
                        	 RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                         }else{
                          	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                          }
                         RegisterFile.updateRegister(operands[0], dif);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rsubk R1,R2,-100",
                	 "Subtraction with Keep: set R1 to (immeditate 16bit sign extended minus R2) and keep carrybit",
                    BasicInstructionFormat.I_FORMAT,
                    "001101 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int sub2 = RegisterFile.getValue(operands[1]);
                         int sub1 = sext(operands[2]);
                         int dif = sub1 - sub2;
                         RegisterFile.updateRegister(operands[0], dif);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rsubkc R1,R2,-100",
                	 "Subtraction with Keep and Carry: set R1 to (immeditate 16bit sign extended minus R2) plus Carry and keep carrybit",
                    BasicInstructionFormat.I_FORMAT,
                    "001111 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         int sub2 = RegisterFile.getValue(operands[1]);
                         int sub1 = sext(operands[2]);
                         int carry = RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & (1 << 29);
                         int dif = sub1 - sub2 + carry;
                         RegisterFile.updateRegister(operands[0], dif);
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rtbd R1,-100",
                    "Return from Break: Jump to adress (R1 plus IMM) sign extended to 32bit and set the Breakbit in the MSR to zero",
                	 BasicInstructionFormat.I_FORMAT,
                    "101101 10010 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processJump(RegisterFile.getValue(operands[0]) + sext(operands[1]));
                         RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(0 << 28)));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rtid R1,-100",
                    "Return from Break: Jump to adress (R1 plus IMM) sign extended to 32bit and set the Interruptbit in the MSR to one",
                	 BasicInstructionFormat.I_FORMAT,
                    "101101 10001 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processJump(RegisterFile.getValue(operands[0]) + sext(operands[1]));
                         RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 30)));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("rtsd R1,-100",
                    "Return from Break: Jump to adress (R1 plus IMM) sign extended to 32bit",
                	 BasicInstructionFormat.I_FORMAT,
                    "101101 10100 fffff ssssssssssssssss",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         Globals.getSettings().setBooleanSetting(Settings.DELAYED_BRANCHING_ENABLED, true);
                         processJump(RegisterFile.getValue(operands[0]) + sext(operands[1]));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("sb R1,R2,R3",
                    "Store byte : Store the low-order 8 bits of R1 into the effective memory byte address of (R2 plus R3)",
                	 BasicInstructionFormat.R_FORMAT,
                    "110100 fffff sssss ttttt 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            Globals.memory.setByte(
                                RegisterFile.getValue(operands[2])
                             +  RegisterFile.getValue(operands[1]),
                                        RegisterFile.getValue(operands[0]) & 0x000000ff);
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("sbi R1,R2,-100",
                    "Store byte : Store the low-order 8 bits of R1 into the effective memory byte address of (R2 plus IMM)",
                	 BasicInstructionFormat.I_FORMAT,
                    "111100 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            Globals.memory.setByte(
                                RegisterFile.getValue(operands[1])
                             +  sext(operands[2]),
                                        RegisterFile.getValue(operands[0]) & 0x000000ff);
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("sext16 R1,R2",
                    "Sign Extend Halfword: sign extended the content of  R2 to 32bit",
                	 BasicInstructionFormat.R_FORMAT,
                    "100100 fffff sssss 0000000001100001",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0], sext(RegisterFile.getValue(operands[1])));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("sext8 R1,R2",
                    "Sign Extend Halfword: sign extended the content of  R2 to 32bit",
                	 BasicInstructionFormat.R_FORMAT,
                    "100100 fffff sssss 0000000001100000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0], (RegisterFile.getValue(operands[1]) << 24 >> 24));
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("sh R1,R2,R3",
                    "Store halfword : Store the low-order 16 bits of R1 into the effective memory halfword address of (R2 plus R3)",
                	 BasicInstructionFormat.R_FORMAT,
                    "110101 fffff sssss ttttt 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            Globals.memory.setHalf(
                                RegisterFile.getValue(operands[2])
                                + RegisterFile.getValue(operands[1]),
                                        RegisterFile.getValue(operands[0])
                                                & 0x0000ffff);
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));		
            
            instructionList.add(
                    new BasicInstruction("shi R1,R2,R3",
                    "Store halfword : Store the low-order 16 bits of R1 into the effective memory halfword address of (R2 plus IMM)",
                	 BasicInstructionFormat.I_FORMAT,
                    "111101 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            Globals.memory.setHalf(
                                RegisterFile.getValue(operands[1])
                                + sext(operands[2]),
                                        RegisterFile.getValue(operands[0])
                                                & 0x0000ffff);
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));		
            
            
            instructionList.add(
                    new BasicInstruction("sw R1,R2,R3",
                    "Store word : Store contents of R1 into effective memory word address of (R2 plus R3)",
                	 BasicInstructionFormat.R_FORMAT,
                    "110110 fffff sssss ttttt 00000000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            Globals.memory.setWord(
                                RegisterFile.getValue(operands[2]) + RegisterFile.getValue(operands[1]),
                                RegisterFile.getValue(operands[0]));
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("swi R1,R2,-100",
                    "Store word : Store contents of R1 into effective memory word address of (R2 plus IMM)",
                	 BasicInstructionFormat.I_FORMAT,
                    "111110 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         try
                         {
                            Globals.memory.setWord(
                                RegisterFile.getValue(operands[1]) + sext(operands[2]),
                                RegisterFile.getValue(operands[0]));
                         } 
                             catch (AddressErrorException e)
                            {
                               throw new ProcessingException(statement, e);
                            }
                      }
                   }));
            
            instructionList.add(
                    new BasicInstruction("xor R1,R2,R3",
                	 "Bitwise XOR (exclusive OR) : Set R1 to bitwise XOR of R2 and R3",
                    BasicInstructionFormat.R_FORMAT,
                    "100010 fffff sssss ttttt 00000 000000",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0],
                            RegisterFile.getValue(operands[1])
                            ^ RegisterFile.getValue(operands[2]));
                      }
                   }));
             instructionList.add(
                    new BasicInstruction("xori R1,R2,-100",
                	 "Bitwise XOR immediate : Set R1 to bitwise XOR of R2 and signextended 16-bit immediate",
                    BasicInstructionFormat.I_FORMAT,
                    "101010 fffff sssss tttttttttttttttt",
                    new SimulationCode()
                   {
                       public void simulate(ProgramStatement statement) throws ProcessingException
                      {
                         int[] operands = statement.getOperands();
                         RegisterFile.updateRegister(operands[0],
                            RegisterFile.getValue(operands[1])
                            ^ sext(operands[2]));
                      }
                   }));	
             
             instructionList.add(
                     new BasicInstruction("sra R1,R2",
                     "Shift right arithmetic : Set R1 to result of sign-extended shifting R2 right by one",
                 	 BasicInstructionFormat.R_FORMAT,
                     "100100 fffff sssss 0000000000000000",
                     new SimulationCode()
                    {
                        public void simulate(ProgramStatement statement) throws ProcessingException
                       {
                          int[] operands = statement.getOperands();
                       // must sign-fill, so use ">>".
                          RegisterFile.updateRegister(operands[0],
                             RegisterFile.getValue(operands[1]) >> 1);
                          int tmp = RegisterFile.getValue(operands[1]) & (1 << 31);
                          if(tmp == 1){
                         	 RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                          }else{
                           	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                          }
                       }
                    }));
             
             instructionList.add(
                     new BasicInstruction("src R1,R2",
                     "Shift right arithmetic with carry : Set R1 to result of sign-extended shifting R2 right by one",
                 	 BasicInstructionFormat.R_FORMAT,
                     "100100 fffff sssss 0000000000000000",
                     new SimulationCode()
                    {
                        public void simulate(ProgramStatement statement) throws ProcessingException
                       {
                          int[] operands = statement.getOperands();
                       // must sign-fill, so use ">>".
                          int carry = RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & (1 << 29);
                          int op = RegisterFile.getValue(operands[1]);
                          op |= carry << 0;
                          RegisterFile.updateRegister(operands[0],
                             op >> 1);
                          int tmp = op & (1 << 31);
                          if(tmp == 1){
                         	 RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                          }else{
                           	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                          }
                       }
                    }));
             
             instructionList.add(
                     new BasicInstruction("srl R1,R2",
                 	 "Shift right logical : Set R1 to result of shifting R2 right by one",
                     BasicInstructionFormat.R_FORMAT,
                     "100100 fffff sssss 0000000000000000",
                     new SimulationCode()
                    {
                        public void simulate(ProgramStatement statement) throws ProcessingException
                       {
                          int[] operands = statement.getOperands();
                       // must zero-fill, so use ">>>" instead of ">>".
                          RegisterFile.updateRegister(operands[0],
                             RegisterFile.getValue(operands[1]) >>> 1);
                          int tmp = RegisterFile.getValue(operands[1]) & (1 << 31);
                          if(tmp == 1){
                         	 RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) | (1 << 29)));
                          }else{
                           	RegisterFile.updateRegister(RegisterFile.MACHINE_STATUS_REGISTER,(RegisterFile.getValue(RegisterFile.MACHINE_STATUS_REGISTER) & ~(1 << 29)));
                          }
                       }
                    }));

        ////////////// READ PSEUDO-INSTRUCTION SPECS FROM DATA FILE AND ADD //////////////////////
         //addPseudoInstructions();
      	
        ////////////// GET AND CREATE LIST OF SYSCALL FUNCTION OBJECTS ////////////////////
         syscallLoader = new SyscallLoader();
         syscallLoader.loadSyscalls();
      	
        // Initialization step.  Create token list for each instruction example.  This is
        // used by parser to determine user program correct syntax.
         for (int i = 0; i < instructionList.size(); i++)
         {
            Instruction inst = (Instruction) instructionList.get(i);
            inst.createExampleTokenList();
         }

		 HashMap maskMap = new HashMap();
		 ArrayList matchMaps = new ArrayList();
		 for (int i = 0; i < instructionList.size(); i++) {
		 	Object rawInstr = instructionList.get(i);
			if (rawInstr instanceof BasicInstruction) {
				BasicInstruction basic = (BasicInstruction) rawInstr;
				Integer mask = Integer.valueOf(basic.getOpcodeMask());
				Integer match = Integer.valueOf(basic.getOpcodeMatch());
				HashMap matchMap = (HashMap) maskMap.get(mask);
				if (matchMap == null) {
					matchMap = new HashMap();
					maskMap.put(mask, matchMap);
					matchMaps.add(new MatchMap(mask, matchMap));
				}
				matchMap.put(match, basic);
			}
		 }
		 Collections.sort(matchMaps);
		 this.opcodeMatchMaps = matchMaps;
      }

	public BasicInstruction findByBinaryCode(int binaryInstr) {
		ArrayList matchMaps = this.opcodeMatchMaps;
		for (int i = 0; i < matchMaps.size(); i++) {
			MatchMap map = (MatchMap) matchMaps.get(i);
			BasicInstruction ret = map.find(binaryInstr);
			if (ret != null) return ret;
		}
		return null;
	}
   	
    /*  METHOD TO ADD PSEUDO-INSTRUCTIONS
    */
   
       private void addPseudoInstructions()
      {
         InputStream is = null;
         BufferedReader in = null;
         try
         {
            // leading "/" prevents package name being prepended to filepath.
            is = this.getClass().getResourceAsStream("/PseudoOps.txt");
            in = new BufferedReader(new InputStreamReader(is));
         } 
             catch (NullPointerException e)
            {
               System.out.println(
                    "Error: MIPS pseudo-instruction file PseudoOps.txt not found.");
               System.exit(0);
            }
         try
         {
            String line, pseudoOp, template, firstTemplate, token;
            String description;
            StringTokenizer tokenizer;
            while ((line = in.readLine()) != null) {
                // skip over: comment lines, empty lines, lines starting with blank.
               if (!line.startsWith("#") && !line.startsWith(" ")
                        && line.length() > 0)  {  
                  description = "";
                  tokenizer = new StringTokenizer(line, "\t");
                  pseudoOp = tokenizer.nextToken();
                  template = "";
                  firstTemplate = null;
                  while (tokenizer.hasMoreTokens()) {
                     token = tokenizer.nextToken();
                     if (token.startsWith("#")) {  
                        // Optional description must be last token in the line.
                        description = token.substring(1);
                        break;
                     }
                     if (token.startsWith("COMPACT")) {
                        // has second template for Compact (16-bit) memory config -- added DPS 3 Aug 2009
                        firstTemplate = template;
                        template = "";
                        continue;
                     } 
                     template = template + token;
                     if (tokenizer.hasMoreTokens()) {
                        template = template + "\n";
                     }
                  }
                  ExtendedInstruction inst = (firstTemplate == null)
                         ? new ExtendedInstruction(pseudoOp, template, description)
                     	 : new ExtendedInstruction(pseudoOp, firstTemplate, template, description);
                  instructionList.add(inst);
               	//if (firstTemplate != null) System.out.println("\npseudoOp: "+pseudoOp+"\ndefault template:\n"+firstTemplate+"\ncompact template:\n"+template);
               }
            }
            in.close();
         } 
             catch (IOException ioe)
            {
               System.out.println(
                    "Internal Error: MIPS pseudo-instructions could not be loaded.");
               System.exit(0);
            } 
             catch (Exception ioe)
            {
               System.out.println(
                    "Error: Invalid MIPS pseudo-instruction specification.");
               System.exit(0);
            }
      
      }
   	
    /**
     *  Given an operator mnemonic, will return the corresponding Instruction object(s)
     *  from the instruction set.  Uses straight linear search technique.
     *  @param name operator mnemonic (e.g. addi, sw,...)
     *  @return list of corresponding Instruction object(s), or null if not found.
     */
       public ArrayList matchOperator(String name)
      {
         ArrayList matchingInstructions = null;
        // Linear search for now....
         for (int i = 0; i < instructionList.size(); i++)
         {
            if (((Instruction) instructionList.get(i)).getName().equalsIgnoreCase(name))
            {
               if (matchingInstructions == null) 
                  matchingInstructions = new ArrayList();
               matchingInstructions.add(instructionList.get(i));
            }
         }
         return matchingInstructions;
      }
   
   
    /**
     *  Given a string, will return the Instruction object(s) from the instruction
     *  set whose operator mnemonic prefix matches it.  Case-insensitive.  For example
     *  "s" will match "sw", "sh", "sb", etc.  Uses straight linear search technique.
     *  @param name a string
     *  @return list of matching Instruction object(s), or null if none match.
     */
       public ArrayList prefixMatchOperator(String name)
      {
         ArrayList matchingInstructions = null;
        // Linear search for now....
         if (name != null) {
            for (int i = 0; i < instructionList.size(); i++)
            {
               if (((Instruction) instructionList.get(i)).getName().toLowerCase().startsWith(name.toLowerCase()))
               {
                  if (matchingInstructions == null) 
                     matchingInstructions = new ArrayList();
                  matchingInstructions.add(instructionList.get(i));
               }
            }
         }
         return matchingInstructions;
      }
   	
   	/*
   	 * Method to find and invoke a syscall given its service number.  Each syscall
   	 * function is represented by an object in an array list.  Each object is of
   	 * a class that implements Syscall or extends AbstractSyscall.
   	 */
   	 
       private void findAndSimulateSyscall(int number, ProgramStatement statement) 
                                                        throws ProcessingException {
         Syscall service = syscallLoader.findSyscall(number);
         if (service != null) {
            service.simulate(statement);
            return;
         }
         throw new ProcessingException(statement,
              "invalid or unimplemented syscall service: " +
              number + " ", Exceptions.SYSCALL_EXCEPTION);
      }
   	
   	/*
   	 * Method to process a successful branch condition.  DO NOT USE WITH JUMP
   	 * INSTRUCTIONS!  The branch operand is a relative displacement in words
   	 * whereas the jump operand is an absolute address in bytes.
   	 *
   	 * The parameter is displacement operand from instruction.
   	 *
   	 * Handles delayed branching if that setting is enabled.
   	 */
   	 // 4 January 2008 DPS:  The subtraction of 4 bytes (instruction length) after
   	 // the shift has been removed.  It is left in as commented-out code below.
   	 // This has the effect of always branching as if delayed branching is enabled, 
   	 // even if it isn't.  This mod must work in conjunction with
   	 // ProgramStatement.java, buildBasicStatementFromBasicInstruction() method near
   	 // the bottom (currently line 194, heavily commented).
   	 
       private void processBranch(int displacement) {
         if (Globals.getSettings().getDelayedBranchingEnabled()) {
            // Register the branch target address (absolute byte address).
            DelayedBranch.register(RegisterFile.getProgramCounter() + displacement  - Instruction.INSTRUCTION_LENGTH);
         } 
         else {
            // Decrement needed because PC has already been incremented
            RegisterFile.setProgramCounter(
                RegisterFile.getProgramCounter()
                  + displacement  - Instruction.INSTRUCTION_LENGTH);	
         }	 
      }
   
   	/*
   	 * Method to process a jump.  DO NOT USE WITH BRANCH INSTRUCTIONS!  
   	 * The branch operand is a relative displacement in words
   	 * whereas the jump operand is an absolute address in bytes.
   	 *
   	 * The parameter is jump target absolute byte address.
   	 *
   	 * Handles delayed branching if that setting is enabled.
   	 */
   	 
       private void processJump(int targetAddress) {
         if (Globals.getSettings().getDelayedBranchingEnabled()) {
            DelayedBranch.register(targetAddress);
         } 
         else {
            RegisterFile.setProgramCounter(targetAddress);
         }	 
      }
       
       /*
      	 * Method to sign extend an imm to 16bit
      	 *
      	 * The parameter is the imm
      	 *
      	 * Returns extended imm
      	 */
       
       private int sext(int imm){
    	   // TODO: clear imm if next instruction is not type B
    	   int tmp_imm = RegisterFile.getValue(RegisterFile.IMMEDIATE_TEMP_REGISTER);
    	   if(tmp_imm>0){
    		   RegisterFile.updateRegister(RegisterFile.IMMEDIATE_TEMP_REGISTER, 0);
    		   return (tmp_imm << 16)+imm;
    	   }else
    		   return (imm << 16 >> 16);
       }
       
       private int sext32(int instr){
    	   int value = (0x0000FFFF & instr);
    	    int mask = 0x00008000;
    	    int sign = (mask & instr) >> 15;
    	    if (sign == 1)
    	        value += 0xFFFF0000;
    	    return value;
       }
   
   	/*
   	 * Method to process storing of a return address in the given
   	 * register.  This is used only by the "and link"
   	 * instructions: jal, jalr, bltzal, bgezal.  If delayed branching
   	 * setting is off, the return address is the address of the
   	 * next instruction (e.g. the current PC value).  If on, the
   	 * return address is the instruction following that, to skip over
   	 * the delay slot.
   	 *
   	 * The parameter is register number to receive the return address.
   	 */
   	 
       private void processReturnAddress(int register) {
         RegisterFile.updateRegister(register, RegisterFile.getProgramCounter() +
                 ((Globals.getSettings().getDelayedBranchingEnabled()) ? 
            	  Instruction.INSTRUCTION_LENGTH : 0) );	 
      }

	  private static class MatchMap implements Comparable {
	  	private int mask;
		private int maskLength; // number of 1 bits in mask
		private HashMap matchMap;

		public MatchMap(int mask, HashMap matchMap) {
			this.mask = mask;
			this.matchMap = matchMap;

			int k = 0;
			int n = mask;
			while (n != 0) {
				k++;
				n &= n - 1;
			}
			this.maskLength = k;
		}

		public boolean equals(Object o) {
			return o instanceof MatchMap && mask == ((MatchMap) o).mask;
		}

		public int compareTo(Object other) {
			MatchMap o = (MatchMap) other;
			int d = o.maskLength - this.maskLength;
			if (d == 0) d = this.mask - o.mask;
			return d;
		}

		public BasicInstruction find(int instr) {
			int match = Integer.valueOf(instr & mask);
			return (BasicInstruction) matchMap.get(match);
		}
	}
   }


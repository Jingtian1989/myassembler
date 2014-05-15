package org.my.asm.stack;

import org.my.asm.bytecode.BytecodeDefinition;
import org.my.asm.bytecode.BytecodeInstruction;

public class StackMachineDefinition implements BytecodeDefinition {

	public static final short INSTR_IADD = 1; // int add
	public static final short INSTR_ISUB = 2;
	public static final short INSTR_IMUL = 3;
	public static final short INSTR_ILT = 4; // int less than
	public static final short INSTR_IEQ = 5; // int equal
	public static final short INSTR_FADD = 6; // float add
	public static final short INSTR_FSUB = 7;
	public static final short INSTR_FMUL = 8;
	public static final short INSTR_FLT = 9; // float less than
	public static final short INSTR_FEQ = 10;
	public static final short INSTR_ITOF = 11; // int to float
	public static final short INSTR_CALL = 12;
	public static final short INSTR_RET = 13; // return with/without value
	public static final short INSTR_BR = 14; // branch
	public static final short INSTR_BRT = 15; // branch if true
	public static final short INSTR_BRF = 16; // branch if true
	public static final short INSTR_CCONST = 17; // push constant char
	public static final short INSTR_ICONST = 18; // push constant integer
	public static final short INSTR_FCONST = 19; // push constant float
	public static final short INSTR_SCONST = 20; // push constant string
	public static final short INSTR_LOAD = 21; // load from local context
	public static final short INSTR_GLOAD = 22; // load from global memory
	public static final short INSTR_FLOAD = 23; // field load
	public static final short INSTR_STORE = 24; // storein local context
	public static final short INSTR_GSTORE = 25; // store in global memory
	public static final short INSTR_FSTORE = 26; // field store
	public static final short INSTR_PRINT = 27; // print stack top
	public static final short INSTR_STRUCT = 28; // push new struct on stack
	public static final short INSTR_NULL = 29; // push null onto stack
	public static final short INSTR_POP = 30; // throw away top of stack
	public static final short INSTR_HALT = 31;

	public static BytecodeInstruction[] instructions = new BytecodeInstruction[] {
			null, // <INVALID>
			new BytecodeInstruction("iadd"), // index is the opcode
			new BytecodeInstruction("isub"), new BytecodeInstruction("imul"),
			new BytecodeInstruction("ilt"), new BytecodeInstruction("ieq"),
			new BytecodeInstruction("fadd"), new BytecodeInstruction("fsub"),
			new BytecodeInstruction("fmul"), new BytecodeInstruction("flt"),
			new BytecodeInstruction("feq"), new BytecodeInstruction("itof"),
			new BytecodeInstruction("call", FUNCTION),
			new BytecodeInstruction("ret"), new BytecodeInstruction("br", INT),
			new BytecodeInstruction("brt", INT),
			new BytecodeInstruction("brf", INT),
			new BytecodeInstruction("cconst", INT),
			new BytecodeInstruction("iconst", INT),
			new BytecodeInstruction("fconst", POOL),
			new BytecodeInstruction("sconst", POOL),
			new BytecodeInstruction("load", INT),
			new BytecodeInstruction("gload", INT),
			new BytecodeInstruction("fload", INT),
			new BytecodeInstruction("store", INT),
			new BytecodeInstruction("gstore", INT),
			new BytecodeInstruction("fstore", INT),
			new BytecodeInstruction("print"),
			new BytecodeInstruction("struct", INT),
			new BytecodeInstruction("null"), new BytecodeInstruction("pop"),
			new BytecodeInstruction("halt") };

	@Override
	public BytecodeInstruction[] getBytecodeInstructions() {
		return instructions;
	}

}

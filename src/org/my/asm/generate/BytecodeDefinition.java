package org.my.asm.generate;

public class BytecodeDefinition {

	public static final int REG = 0;
	public static final int FUNCTION = 0;
	public static final int INT = 0;

	public static final int INSTR_ADD = 1;
	// ...

	public static BytecodeInstruction[] instructions = new BytecodeInstruction[] {
			null, new BytecodeInstruction("iadd", REG, REG, REG) };

}

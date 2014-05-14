package org.my.asm.bytecode;

public abstract class BytecodeDefinition {

	public static final int REG = 0;
	public static final int FUNCTION = 0;
	public static final int INT = 0;
	public static final int POOL = 1000;
	
	public abstract BytecodeInstruction[] getBytecodeInstructions();

}

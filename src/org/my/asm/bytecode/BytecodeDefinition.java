package org.my.asm.bytecode;

public interface BytecodeDefinition {

	public static final int REG = 0;
	public static final int FUNCTION = 1;
	public static final int INT = 2;
	public static final int POOL = 1000;
	
	public BytecodeInstruction[] getBytecodeInstructions();

}

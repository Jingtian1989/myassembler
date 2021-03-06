package org.my.asm.bytecode;

public class BytecodeInstruction {

	private String name;
	private int[] type = new int[3];
	private int operandCount = 0;

	public BytecodeInstruction(String name) {
		this(name, 0, 0, 0);
		operandCount = 0;
	}

	public BytecodeInstruction(String name, int op1) {
		this(name, op1, 0, 0);
		operandCount = 1;
	}

	public BytecodeInstruction(String name, int op1, int op2) {
		this(name, op1, op2, 0);
		operandCount = 2;
	}

	public BytecodeInstruction(String name, int op1, int op2, int op3) {
		this.name = name;
		type[0] = op1;
		type[1] = op2;
		type[2] = op3;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOperandCount() {
		return operandCount;
	}

	public void setOperandCount(int operandCount) {
		this.operandCount = operandCount;
	}

	public int getOperandType(int index) {
		return type[index];
	}

}

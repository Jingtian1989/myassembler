package org.my.asm.stack;

import org.my.asm.bytecode.BytecodeAssembler;
import org.my.asm.bytecode.FunctionSymbol;

public class StackInterpreter {

	public static final int DEFAULT_OPERAND_STACK_SIZE = 100;
	public static final int DEFAULT_CALL_STACK_SIZE = 1000;

	private int ip;
	private byte[] code;
	private int codeSize;
	private Object[] globals;
	private Object[] constPool;
	private Object[] operands = new Object[DEFAULT_OPERAND_STACK_SIZE];
	private int sp = -1;
	private StackFrame[] calls = new StackFrame[DEFAULT_CALL_STACK_SIZE];
	private int fp = -1;
	private FunctionSymbol mainFunction;
	private boolean trace = false;

	public StackInterpreter(BytecodeAssembler assember) {
		this.code = assember.getMachineCode();
		this.codeSize = assember.getCodeMemorySize();
		this.constPool = assember.getConstantPool();
		this.mainFunction = assember.getMainFunction();
		this.globals = new Object[assember.getDataSize()];
	}

	public void execute() {
		StackFrame functionFrame = new StackFrame(mainFunction, -1);
		calls[++fp] = functionFrame;
		ip = mainFunction.getAddress();
		cpu();
	}

	private void cpu() {
		short opcode = code[ip];
		int iop1, iop2;
		float fop1, fop2;
		while (opcode != StackMachineDefinition.INSTR_HALT && ip < codeSize) {
			ip++;
			switch (opcode) {
			case StackMachineDefinition.INSTR_IADD:
				iop1 = (Integer) operands[sp - 1];
				iop2 = (Integer) operands[sp];
				sp -= 2;
				operands[++sp] = iop1 + iop2;
				break;
			case StackMachineDefinition.INSTR_ISUB:
				iop1 = (Integer) operands[sp - 1];
				iop2 = (Integer) operands[sp];
				sp -= 2;
				operands[++sp] = iop1 - iop2;
				break;
			}
		}
	}
}

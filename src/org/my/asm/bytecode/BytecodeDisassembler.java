package org.my.asm.bytecode;

import java.util.ArrayList;
import java.util.List;

public class BytecodeDisassembler {

	private byte[] code;
	private int codeSize;
	private Object[] constPool;
	private BytecodeDefinition bytecodeDefinition;

	public BytecodeDisassembler(byte[] code, int codeSize, Object[] constPool) {
		this.code = code;
		this.codeSize = codeSize;
		this.constPool = constPool;
	}

	public void disassemble() {
		System.out.println("Disassembly:");
		int ip = 0;
		while (ip < codeSize) {
			ip = disassembleInstruction(ip);
			System.out.println();
		}
		System.out.println();
	}

	private int disassembleInstruction(int ip) {
		int opCode = code[ip];
		BytecodeInstruction instruction = bytecodeDefinition
				.getBytecodeInstructions()[opCode];
		String instructionName = instruction.getName();
		System.out.printf("%04d:\t%-11s", ip, instructionName);
		ip++;
		if (instruction.getOperandCount() == 0) {
			System.out.print("	");
		}
		List<String> operandStrings = new ArrayList<String>();
		for (int i = 0; i < instruction.getOperandCount(); i++) {
			int operand = BytecodeAssembler.readInt(code, ip);
			ip += 4;
			switch (instruction.getOperandType(i)) {
			case BytecodeDefinition.REG:
				operandStrings.add("r" + operand);
				break;
			case BytecodeDefinition.FUNCTION:
			case BytecodeDefinition.POOL:
				operandStrings.add(showConstPoolOperand(operand));
				break;
			case BytecodeDefinition.INT:
				operandStrings.add(String.valueOf(operand));
				break;
			}
		}
		for (int i = 0; i < operandStrings.size(); i++) {
			String string = (String) operandStrings.get(i);
			if (i > 0) {
				System.out.print(string);
			}
		}
		return ip;
	}

	private String showConstPoolOperand(int poolIndex) {
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		sb.append(poolIndex);
		String string = constPool[poolIndex].toString();
		if (constPool[poolIndex] instanceof String) {
			string = '"' + string + '"';
		} else if (constPool[poolIndex] instanceof FunctionSymbol) {
			FunctionSymbol functionSymbol = (FunctionSymbol) constPool[poolIndex];
			string = functionSymbol.getName() + "()@"
					+ functionSymbol.getAddress();
		}
		sb.append(":");
		sb.append(string);
		return sb.toString();
	}
}

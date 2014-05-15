package org.my.asm.stack;

import org.my.asm.bytecode.BytecodeAssembler;
import org.my.asm.bytecode.FunctionSymbol;
import org.my.asm.exception.AssemblerUnknowInstructionException;

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

	public StackInterpreter() {
	}

	public void execute(byte[] code, int codeSize, Object[] constPool,
			FunctionSymbol mainFunctionSymbol, int dataSize)
			throws AssemblerUnknowInstructionException {
		this.code = code;
		this.codeSize = codeSize;
		this.constPool = constPool;
		this.mainFunction = mainFunctionSymbol;
		this.globals = new Object[dataSize];

		StackFrame functionFrame = new StackFrame(mainFunction, -1);
		calls[++fp] = functionFrame;
		ip = mainFunction.getAddress();
		cpu();
	}

	private void cpu() throws AssemblerUnknowInstructionException {
		short opcode = code[ip];
		int iop1, iop2;
		float fop1, fop2;
		int address;
		Object value;
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
			case StackMachineDefinition.INSTR_IMUL:
				iop1 = (Integer) operands[sp - 1];
				iop2 = (Integer) operands[sp];
				sp -= 2;
				operands[++sp] = iop1 * iop2;
				break;
			case StackMachineDefinition.INSTR_ILT:
				iop1 = (Integer) operands[sp - 1];
				iop2 = (Integer) operands[sp];
				sp -= 2;
				operands[++sp] = iop1 < iop2;
				break;
			case StackMachineDefinition.INSTR_IEQ:
				iop1 = (Integer) operands[sp - 1];
				iop2 = (Integer) operands[sp];
				sp -= 2;
				operands[++sp] = iop1 == iop2;
				break;
			case StackMachineDefinition.INSTR_FADD:
				fop1 = (Float) operands[sp - 1];
				fop2 = (Float) operands[sp];
				sp -= 2;
				operands[++sp] = fop1 + fop2;
				break;
			case StackMachineDefinition.INSTR_FSUB:
				fop1 = (Float) operands[sp - 1];
				fop2 = (Float) operands[sp];
				sp -= 2;
				operands[++sp] = fop1 - fop2;
				break;
			case StackMachineDefinition.INSTR_FMUL:
				fop1 = (Float) operands[sp - 1];
				fop2 = (Float) operands[sp];
				sp -= 2;
				operands[++sp] = fop1 * fop2;
				break;
			case StackMachineDefinition.INSTR_FLT:
				fop1 = (Float) operands[sp - 1];
				fop2 = (Float) operands[sp];
				sp -= 2;
				operands[++sp] = fop1 < fop2;
				break;
			case StackMachineDefinition.INSTR_FEQ:
				fop1 = (Float) operands[sp - 1];
				fop2 = (Float) operands[sp];
				sp -= 2;
				operands[++sp] = fop1 == fop2;
				break;
			case StackMachineDefinition.INSTR_ITOF:
				iop1 = (Integer) operands[sp--];
				operands[++sp] = (float) iop1;
				break;
			case StackMachineDefinition.INSTR_CALL:
				int functionIndexInConstPool = getIntOperand();
				call(functionIndexInConstPool);
				break;
			case StackMachineDefinition.INSTR_RET:
				StackFrame functionFrame = calls[fp--];
				ip = functionFrame.getReturnAddres();
				break;
			case StackMachineDefinition.INSTR_BR:
				ip = getIntOperand();
				break;
			case StackMachineDefinition.INSTR_BRT:
				address = getIntOperand();
				if (operands[sp--].equals(true)) {
					ip = address;
				}
				break;
			case StackMachineDefinition.INSTR_CCONST:
				operands[++sp] = (char) getIntOperand();
				break;
			case StackMachineDefinition.INSTR_ICONST:
				operands[++sp] = getIntOperand();
				break;
			case StackMachineDefinition.INSTR_FCONST:
			case StackMachineDefinition.INSTR_SCONST:
				int constPoolIndex = getIntOperand();
				operands[++sp] = constPool[constPoolIndex];
				break;
			case StackMachineDefinition.INSTR_LOAD: // load from call stack
				address = getIntOperand();
				operands[++sp] = calls[fp].getLocal(address);
				break;
			case StackMachineDefinition.INSTR_GLOAD: // load from global stack
				address = getIntOperand();
				operands[++sp] = globals[address];
				break;
			case StackMachineDefinition.INSTR_FLOAD:
				StructSpace structSpace = (StructSpace) operands[sp--];
				int fieldOffset = getIntOperand();
				operands[++sp] = structSpace.getField(fieldOffset);
				break;
			case StackMachineDefinition.INSTR_STORE:
				address = getIntOperand();
				calls[fp].addLocal(operands[sp--], address);
				break;
			case StackMachineDefinition.INSTR_GSTORE:
				address = getIntOperand();
				globals[address] = operands[sp--];
				break;
			case StackMachineDefinition.INSTR_FSTORE:
				structSpace = (StructSpace) operands[sp--];
				value = operands[sp--];
				fieldOffset = getIntOperand();
				structSpace.setField(fieldOffset, value);
				break;
			case StackMachineDefinition.INSTR_PRINT:
				System.out.println(operands[sp--]);
				break;
			case StackMachineDefinition.INSTR_STRUCT:
				int fieldCount = getIntOperand();
				operands[++sp] = new StructSpace(fieldCount);
				break;
			case StackMachineDefinition.INSTR_NULL:
				operands[++sp] = null;
				break;
			case StackMachineDefinition.INSTR_POP:
				--sp;
				break;
			default:
				throw new AssemblerUnknowInstructionException(
						"runtime error, unknow opcode '" + opcode + "\'");
			}
			opcode = code[ip];
		}
	}

	private void call(int functionIndexInConstPool) {
		FunctionSymbol functionSymbol = (FunctionSymbol) constPool[functionIndexInConstPool];
		StackFrame functionFrame = new StackFrame(functionSymbol, ip);
		calls[++fp] = functionFrame;
		for (int i = functionSymbol.getArgCount() - 1; i >= 0; i--) {
			functionFrame.addLocal(operands[sp--], i);
		}
		ip = functionSymbol.getAddress();
	}

	private int getIntOperand() {
		int operand = BytecodeAssembler.readInt(code, ip);
		ip += 4;
		return operand;
	}
}

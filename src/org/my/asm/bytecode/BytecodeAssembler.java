package org.my.asm.bytecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.my.asm.lex.Tag;
import org.my.asm.lex.Token;
import org.my.asm.lex.AssemblerLexer;
import org.my.asm.parse.AssemblerParser;
import org.my.asm.exception.AssemblerRedefinitionException;
import org.my.asm.exception.AssemblerUndefinitionException;
import org.my.asm.exception.AssemblerUnknowInstructionException;

public class BytecodeAssembler extends AssemblerParser {

	public static final int INITIAL_CODE_SIZE = 1024;
	private Map<String, Integer> instructionOpcodeMapping = new HashMap<String, Integer>();
	private Map<String, LabelSymbol> labels = new HashMap<String, LabelSymbol>();
	private List<Object> constPool = new ArrayList<Object>();
	private byte[] code = new byte[INITIAL_CODE_SIZE];
	private int ip = 0;

	private int dataSize; // .globals
	private FunctionSymbol mainFunctionSymbol;

	public BytecodeAssembler(AssemblerLexer lexer,
			BytecodeInstruction[] instructions) {
		super(lexer);

		for (int i = 1; i < instructions.length; i++) {
			instructionOpcodeMapping.put(instructions[i].getName(), i);
		}
	}

	public void generateInstruct(String insName)
			throws AssemblerUnknowInstructionException {
		Integer opcode = instructionOpcodeMapping.get(insName);
		if (opcode == null) {
			throw new AssemblerUnknowInstructionException("asm error at line "
					+ AssemblerLexer.line + ", unknow instruct '" + insName
					+ "\'");
		}
		int opcode2 = opcode.intValue();
		ensureCapacity(ip + 1);
		code[ip++] = (byte) (opcode & 0xff);

	}

	public void generateInstruct(String insName, Token operand)
			throws AssemblerUnknowInstructionException {
		generateInstruct(insName);
		generateOperand(operand);
	}

	public void generateInstruct(String insName, Token operand1, Token operand2)
			throws AssemblerUnknowInstructionException {
		generateInstruct(insName, operand1);
		generateOperand(operand2);
	}

	public void generateInstruct(String insName, Token operand1,
			Token operand2, Token operand3)
			throws AssemblerUnknowInstructionException {
		generateInstruct(insName, operand1, operand2);
		generateOperand(operand3);
	}

	private void generateOperand(Token operand)
			throws AssemblerUnknowInstructionException {
		int v = 0;
		String value = operand.getValue();
		switch (operand.getType()) {
		case Tag.INT:
			v = Integer.valueOf(value);
			break;
		case Tag.CHAR:
			v = Character.valueOf(value.charAt(0));
			break;
		case Tag.FLOAT:
		case Tag.STRING:
		case Tag.ID:
			v = getConstantPoolIndex(value);
			break;
		case Tag.FUNCTION:
			v = getFunctionIndex(value);
			break;
		case Tag.REG:
			v = getRegisterNumber(value);
			break;
		default:
			throw new AssemblerUnknowInstructionException("asm error at line "
					+ AssemblerLexer.line + ", unknow operand type '"
					+ operand.getType() + "\'");
		}
		ensureCapacity(ip + 4);
		writeInt(code, ip, v);
		ip = ip + 4;
	}

	public void checkForUnresolvedReferences()
			throws AssemblerUndefinitionException {
		for (String name : labels.keySet()) {
			LabelSymbol labelSymbol = (LabelSymbol) labels.get(name);
			if (!labelSymbol.isDefined()) {
				throw new AssemblerUndefinitionException("asm error at line "
						+ AssemblerLexer.line + ", undefinition of label '"
						+ name + "\'");
			}
		}
	}

	public void defineDataSize(int size) {
		this.dataSize = size;
	}

	public void defineLabel(String label) throws AssemblerRedefinitionException {
		LabelSymbol labelSymbol = labels.get(label);
		if (labelSymbol == null) {
			labelSymbol = new LabelSymbol(label, ip, false);
			labels.put(label, labelSymbol);
		} else {
			if (labelSymbol.isForwardReference()) {
				labelSymbol.setDefined(true);
				labelSymbol.setAddress(ip);
				labelSymbol.resolveForwardReference(code);
			} else {
				throw new AssemblerRedefinitionException("asm error at line "
						+ AssemblerLexer.line + ", redefinition of label '"
						+ label + "\'");
			}
		}
	}

	public void defineFunction(String name, int argCount, int localCount) {
		FunctionSymbol functionSymbol = new FunctionSymbol(name, argCount,
				localCount, ip);
		if (name.equals("main"))
			mainFunctionSymbol = functionSymbol;
		if (constPool.contains(functionSymbol)) {
			constPool.set(constPool.indexOf(functionSymbol), functionSymbol);
		} else {
			getConstantPoolIndex(functionSymbol);
		}
	}

	private int getRegisterNumber(String reg) {
		String regNumber = reg.substring(1);
		return Integer.valueOf(regNumber);
	}

	private int getFunctionIndex(String functionName) {
		int index = constPool.indexOf(new FunctionSymbol(functionName));
		if (index >= 0)
			return index;
		return getConstantPoolIndex(new FunctionSymbol(functionName));
	}

	private int getConstantPoolIndex(Object obj) {
		if (constPool.contains(obj)) {
			return constPool.indexOf(obj);
		}
		constPool.add(obj);
		return constPool.size() - 1;
	}

	private void ensureCapacity(int index) {
		if (index > code.length) {
			int newSize = index * 2;
			byte[] bigger = new byte[newSize];
			System.arraycopy(code, 0, bigger, 0, code.length);
			code = bigger;
		}
	}

	public static void writeInt(byte[] bytes, int address, int value) {
		bytes[address + 0] = (byte) ((value >> (24)) & 0xFF);
		bytes[address + 1] = (byte) ((value >> (16)) & 0xFF);
		bytes[address + 2] = (byte) ((value >> (8)) & 0xFF);
		bytes[address + 3] = (byte) (value & 0xFF);
	}

	public static int readInt(byte[] bytes, int address) {
		int b1 = bytes[address++] & 0xff;
		int b2 = bytes[address++] & 0xff;
		int b3 = bytes[address++] & 0xff;
		int b4 = bytes[address++] & 0xff;
		int ret = (b1 << (24)) | (b2 << 16) | (b3 << 8) | b4;
		return ret;
	}

	public byte[] getMachineCode() {
		return code;
	}

	public int getCodeMemorySize() {
		return ip;
	}

	public Object[] getConstantPool() {
		return constPool.toArray();
	}

	public FunctionSymbol getMainFunction() {
		return mainFunctionSymbol;
	}

	public int getDataSize() {
		return dataSize;
	}
}

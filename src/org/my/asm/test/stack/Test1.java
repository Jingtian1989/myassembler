package org.my.asm.test.stack;

import org.my.asm.bytecode.BytecodeAssembler;
import org.my.asm.bytecode.BytecodeDefinition;
import org.my.asm.exception.AssemblerMissmatchedException;
import org.my.asm.exception.AssemblerRedefinitionException;
import org.my.asm.exception.AssemblerUndefinitionException;
import org.my.asm.exception.AssemblerUnknowInstructionException;
import org.my.asm.lex.AssemblerLexer;
import org.my.asm.parse.AssemblerParser;
import org.my.asm.stack.StackInterpreter;
import org.my.asm.stack.StackMachineDefinition;
import org.my.asm.test.Util;

public class Test1 {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String text = Util
				.readFile("\\src\\org\\my\\asm\\test\\stack\\test1.mcode");
		AssemblerLexer lexer = new AssemblerLexer(text);
		BytecodeDefinition definition = new StackMachineDefinition();
		BytecodeAssembler parser = new BytecodeAssembler(lexer,
				definition.getBytecodeInstructions());
		StackInterpreter interpreter = new StackInterpreter();
		try {
			parser.parse();
			interpreter.execute(parser.getMachineCode(),
					parser.getCodeMemorySize(), parser.getConstantPool(),
					parser.getMainFunction(), parser.getDataSize());
		} catch (AssemblerMissmatchedException | AssemblerRedefinitionException
				| AssemblerUnknowInstructionException
				| AssemblerUndefinitionException e) {
			e.printStackTrace();
		}

	}
}

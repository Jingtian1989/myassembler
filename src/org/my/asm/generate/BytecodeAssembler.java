package org.my.asm.generate;

import org.my.asm.lex.AssemblerLexer;
import org.my.asm.lex.Token;
import org.my.asm.parse.AssemblerParser;

public class BytecodeAssembler extends AssemblerParser {

	public static final int INITIAL_CODE_SIZE = 1024;
	
	
	public BytecodeAssembler(AssemblerLexer lexer) {
		super(lexer);
	}

	public void defineLabel(Token label) {

	}

	public void generateInstruct(String value, String value2, String value3,
			String value4) {

	}

	public void generateInstruct(String value, String value2, String value3) {

	}

	public void generateInstruct(String value, String value2) {

	}

	public void generateInstruct(String value) {

	}

	public void checkForUnresolvedReferences() {

	}

	public void defineDataSize(int size) {

	}

	public void defineFunction(String name, int argCount, int localCount) {

	}

}

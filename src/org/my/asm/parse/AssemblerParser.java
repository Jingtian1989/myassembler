package org.my.asm.parse;

import org.my.asm.exception.AssemblerMissmatchedException;
import org.my.asm.lex.AssemblerLexer;
import org.my.asm.lex.Tag;
import org.my.asm.lex.Token;

public abstract class AssemblerParser {

	public static final int k = 12;

	private AssemblerLexer lexer;
	private int p = 0;
	private Token[] lookahead = new Token[k];

	public AssemblerParser(AssemblerLexer lexer) {
		this.lexer = lexer;
	}

	private void consumeToken() throws AssemblerMissmatchedException {
		lookahead[p] = lexer.nextToken();
		p = (p + 1) % k;
	}

	private Token lookToken(int i) {
		return lookahead[(p + i - 1) % k];
	}

	private int lookAhead(int i) {
		return lookToken(i).getType();
	}

	private void match(int type) throws AssemblerMissmatchedException {
		if (lookAhead(1) != type) {
			throw new AssemblerMissmatchedException("parse error at line "
					+ AssemblerLexer.line + ", expected '"
					+ String.valueOf(type) + "'" + ", but encountered '"
					+ String.valueOf(lookAhead(1)) + "'");
		}
		consumeToken();
	}

	private void init() throws AssemblerMissmatchedException {
		for (int i = 0; i < k; i++) {
			consumeToken();
		}
	}

	public void parse() throws AssemblerMissmatchedException {
		init();
		program();
	}

	private void program() throws AssemblerMissmatchedException {
		globals();
		do {
			if (lookAhead(1) == Tag.DEF) {
				functionDeclaration();
			} else if (lookAhead(1) == Tag.ID) {
				if (lookAhead(2) == ':') {
					label();
				} else {
					instruct();
				}
			} else if (lookAhead(1) == Tag.NEWLINE) {
				consumeToken();
			}
			checkForUnresolvedReferences();
		} while (lookAhead(1) == Tag.DEF || lookAhead(1) == Tag.ID
				|| lookAhead(1) == Tag.NEWLINE);
	}

	private void globals() throws AssemblerMissmatchedException {
		while (lookAhead(1) == Tag.NEWLINE) {
			consumeToken();
		}
		if (lookAhead(1) == Tag.GLOBAL) {
			consumeToken();
			Token globalSize = lookToken(1);
			match(Tag.INT);
			match(Tag.NEWLINE);
			defineDataSize(Integer.valueOf(globalSize.getValue()));
		}
	}

	private void functionDeclaration() throws AssemblerMissmatchedException {
		match(Tag.DEF);
		Token name = lookToken(1);
		match(Tag.ID);
		match(':');
		match(Tag.ARGS);
		match('=');
		Token argCount = lookToken(1);
		match(Tag.INT);
		match(',');
		match(Tag.LOCALS);
		match('=');
		Token localCount = lookToken(1);
		match(Tag.INT);
		match(Tag.NEWLINE);

		defineFunction(name.getValue(), Integer.valueOf(argCount.getValue()),
				Integer.valueOf(localCount.getValue()));
	}

	private void instruct() throws AssemblerMissmatchedException {
		Token insr = lookToken(1);
		match(Tag.ID);
		// ID NEWLINE
		if (lookAhead(1) == Tag.NEWLINE) {
			match(Tag.NEWLINE);
			generateInstruct(insr.getValue());
			return;
		}
		// ID operand NEWLINE
		Token op1 = operand();
		if (lookAhead(1) == Tag.NEWLINE) {
			match(Tag.NEWLINE);
			generateInstruct(insr.getValue(), op1.getValue());
			return;
		}
		// ID operand, operand NEWLINE
		match(',');
		Token op2 = operand();
		if (lookAhead(1) == Tag.NEWLINE) {
			match(Tag.NEWLINE);
			generateInstruct(insr.getValue(), op1.getValue(), op2.getValue());
		}
		// ID operand, operand, operand NEWLINE
		match(',');
		Token op3 = operand();
		match(Tag.NEWLINE);
		generateInstruct(insr.getValue(), op1.getValue(), op2.getValue(),
				op3.getValue());

	}

	private Token operand() throws AssemblerMissmatchedException {
		Token ret = lookToken(1);
		if (lookAhead(1) == Tag.ID) {
			consumeToken();
			if (lookAhead(2) == '(') {
				match('(');
				match(')');
			}
			return ret;
		} else if (lookAhead(1) == Tag.REG) {
			consumeToken();
			return ret;
		} else if (lookAhead(1) == Tag.INT) {
			consumeToken();
			return ret;
		}
		throw new AssemblerMissmatchedException("parse error at line "
				+ AssemblerLexer.line + ", expected an operand"
				+ "but encountered '" + String.valueOf(ret.getType()) + "'");
	}

	private void label() throws AssemblerMissmatchedException {
		Token label = lookToken(1);
		match(':');
		defineLabel(label);
	}

	public abstract void defineLabel(Token label);

	public abstract void generateInstruct(String value, String value2,
			String value3, String value4);

	public abstract void generateInstruct(String value, String value2,
			String value3);

	public abstract void generateInstruct(String value, String value2);

	public abstract void generateInstruct(String value);

	public abstract void checkForUnresolvedReferences();

	public abstract void defineDataSize(int size);

	public abstract void defineFunction(String name, int argCount,
			int localCount);
}

package org.my.asm.lex;

import java.util.HashMap;
import java.util.Map;

import org.my.asm.exception.AssemblerEOFException;
import org.my.asm.exception.AssemblerMissmatchedException;

public class AssemblerLexer {

	private String text;
	private int size;
	private int cursor = -1;
	private char peek = ' ';
	private Map<String, Token> words = new HashMap<String, Token>();

	public static int line = 0;
	public static final Token EOF = new Token(Tag.EOF, "eof");
	public static final Token NEWLINE = new Token(Tag.NEWLINE, "newline");
	public static final Token GLOBAL = new Token(Tag.GLOBAL, ".global");
	public static final Token DEF = new Token(Tag.DEF, ".def");
	public static final Token ARGS = new Token(Tag.ARGS, "args");
	public static final Token LOCALS = new Token(Tag.LOCALS, "locals");

	public AssemblerLexer(String text) {
		this.text = text;
		this.size = text.length();
	}

	private void init() {
		this.words.put("args", AssemblerLexer.ARGS);
		this.words.put("locals", AssemblerLexer.LOCALS);
	}

	private void consumeChar() throws AssemblerEOFException {
		cursor++;
		if (cursor >= size) {
			throw new AssemblerEOFException();
		}
		peek = text.charAt(cursor);
	}

	private boolean isDigit(char peek) {
		if (peek >= '0' && peek <= '9') {
			return true;
		}
		return false;
	}

	private int toInt(char peek) {
		return peek - '0';
	}

	private boolean isLetter(char peek) {
		if ((peek >= 'a' && peek <= 'z') || (peek >= 'A' && peek <= 'Z'))
			return true;
		return false;
	}

	private void match(char c) throws AssemblerMissmatchedException,
			AssemblerEOFException {
		if (peek != c) {
			throw new AssemblerMissmatchedException("lex error at line "
					+ AssemblerLexer.line + ", expected '" + String.valueOf(c)
					+ "'" + ", but encountered '" + String.valueOf(peek) + "'");
		}
		consumeChar();
	}

	private void match(String str) throws AssemblerMissmatchedException,
			AssemblerEOFException {
		int i = 0;
		while (i < str.length()) {
			match(str.charAt(i));
		}
	}

	public Token nextToken() throws AssemblerMissmatchedException {
		Token ret = null;

		try {
			while (peek == ' ' || peek == '\t') {
				consumeChar();
			}
			// NEWLINE (';' .*)? '\r'? '\n'
			if (peek == ';' || peek == '\r' || peek == '\n') {
				if (peek == ';') {
					consumeChar();
					while (peek != '\r' || peek != '\n') {
						consumeChar();
					}
				}
				if (peek == '\r') {
					consumeChar();
				}
				match('\n');
				ret = AssemblerLexer.NEWLINE;
				AssemblerLexer.line++;
				return ret;

			} else if (peek == '-' || isDigit(peek) || peek == '.') {
				int digit = 0;
				int decimal = 0;
				int base = 1;
				boolean isNegative = false;
				boolean isInteger = false;
				boolean isFloat = false;

				if (peek == '-') {
					isNegative = true;
					consumeChar();
				}
				// INT.INT*
				if (isDigit(peek)) {
					isInteger = true;
					while (isDigit(peek)) {
						digit = digit * 10 + toInt(peek);
						consumeChar();
					}
				}
				// .def .global .INT+ INT.INT*
				if (peek == '.') {
					consumeChar();
					// INT.INT*
					if (isInteger || isDigit(peek)) {
						isFloat = true;
					}
					while (isDigit(peek)) {
						decimal = decimal * 10 + toInt(peek);
						base = base * 10;
					}
					// .def .global
					if (!isNegative && !isInteger && !isFloat && isLetter(peek)) {
						if (peek == 'g') {
							match("global");
							ret = AssemblerLexer.GLOBAL;
							return ret;
						} else if (peek == 'd') {
							match("def");
							ret = AssemblerLexer.DEF;
							return ret;
						}
					}
				}
				if (isFloat) {
					float value = (float) digit
							+ ((float) decimal / (float) base);
					value = isNegative ? -value : value;
					ret = new Token(Tag.FLOAT, String.valueOf(value));
					return ret;
				} else if (isInteger) {
					digit = isNegative ? -digit : digit;
					ret = new Token(Tag.INT, String.valueOf(digit));
					return ret;
				}

				throw new AssemblerMissmatchedException("lex error at line "
						+ AssemblerLexer.line
						+ ", unexpected integer or float format");
			} else if (peek == '"') {
				consumeChar();
				StringBuilder sb = new StringBuilder();
				while (peek != '"') {
					sb.append(peek);
					consumeChar();
				}
				match('"');
				ret = new Token(Tag.STRING, sb.toString());
				return ret;
			} else if (peek == '\'') {
				consumeChar();
				char c = peek;
				consumeChar();
				match('\'');
				ret = new Token(Tag.CHAR, String.valueOf(c));
				return ret;
				// 'r' INT; ID
			} else if (isLetter(peek)) {
				StringBuilder sb = new StringBuilder();
				if (peek == 'r') {
					sb.append(peek);
					consumeChar();
					while (isDigit(peek)) {
						sb.append(peek);
						consumeChar();
					}
				}

				if (sb.length() > 1 && !isLetter(peek)) {
					ret = new Token(Tag.REG, sb.toString());
					return ret;
				}

				while (isDigit(peek) || isLetter(peek)) {
					sb.append(peek);
					consumeChar();
				}
				ret = words.get(sb.toString());
				if (ret == null) {
					ret = new Token(Tag.ID, sb.toString());
					words.put(sb.toString(), ret);
				}
				return ret;
			}

			ret = new Token(peek, String.valueOf(peek));
			consumeChar();
			return ret;

		} catch (AssemblerEOFException e) {
			if (ret == null)
				return AssemblerLexer.EOF;
			return ret;
		}

	}
}

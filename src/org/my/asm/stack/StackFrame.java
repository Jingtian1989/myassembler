package org.my.asm.stack;

import org.my.asm.bytecode.FunctionSymbol;

public class StackFrame {

	private FunctionSymbol functionSymbol;
	private int returnAddress;
	private Object[] locals;

	public StackFrame(FunctionSymbol functionSymbol, int returnAddress) {
		this.functionSymbol = functionSymbol;
		this.returnAddress = returnAddress;
		this.locals = new Object[functionSymbol.getArgCount()
				+ functionSymbol.getLocalCount()];
	}

}

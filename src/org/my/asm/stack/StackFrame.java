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

	public void addLocal(Object arg, int index) {
		this.locals[index] = arg;
	}

	public Object getLocal(int index) {
		return locals[index];
	}

	public int getReturnAddres() {
		return returnAddress;
	}

}

package org.my.asm.generate;

public class FunctionSymbol {

	private String name;
	private int argCount;
	private int localCount;
	private int address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getArgCount() {
		return argCount;
	}

	public void setArgCount(int argCount) {
		this.argCount = argCount;
	}

	public int getLocalCount() {
		return localCount;
	}

	public void setLocalCount(int localCount) {
		this.localCount = localCount;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public FunctionSymbol(String name) {
		this.name = name;
	}

	public FunctionSymbol(String name, int argCount, int localCount, int address) {
		this.name = name;
		this.argCount = argCount;
		this.localCount = localCount;
		this.address = address;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object obj) {
		return obj instanceof FunctionSymbol
				&& name.equals(((FunctionSymbol) obj).getName());
	}
}

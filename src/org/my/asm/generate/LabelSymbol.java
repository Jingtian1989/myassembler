package org.my.asm.generate;

import java.util.Vector;

public class LabelSymbol {
	private String name;
	private int address;
	private boolean isForwardReference = false;
	private boolean isDefined = true;
	private Vector<Integer> forwardReferences = new Vector<Integer>();

	public LabelSymbol(String name) {
		this.name = name;
	}

	public LabelSymbol(String name, int address) {
		this.name = name;
		this.address = address;
	}

	public LabelSymbol(String name, int address, boolean forward) {
		this.name = name;
		this.isForwardReference = forward;
		if (forward) {
			addForwardReference(address);
		} else {
			this.address = address;
		}
	}

	private void addForwardReference(int address) {
		forwardReferences.addElement(new Integer(address));
	}

	public void resolveForwardReference(byte[] code) {
		isForwardReference = false;
		for (int addressToPatch : forwardReferences) {
			BytecodeAssembler.writeInt(code, addressToPatch, address);
		}
	}

	public String toString() {
		String refs = "";
		if (forwardReferences != null) {
			refs = "[refs=" + forwardReferences.toString() + "]";
		}
		return name + "@" + address + refs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public boolean isForwardReference() {
		return isForwardReference;
	}

	public void setForwardReference(boolean isForwardReference) {
		this.isForwardReference = isForwardReference;
	}

	public boolean isDefined() {
		return isDefined;
	}

	public void setDefined(boolean isDefined) {
		this.isDefined = isDefined;
	}
}

package org.my.asm.stack;

import java.util.Arrays;

public class StructSpace {

	private Object[] fields;

	public StructSpace(int field_count) {
		fields = new Object[field_count];
	}

	public String toString() {
		return Arrays.toString(fields);
	}

	public Object getField(int offset) {
		return fields[offset];
	}
	
	public void setField(int offset, Object obj){
		fields[offset] = obj;
	}
}

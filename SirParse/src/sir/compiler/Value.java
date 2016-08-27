package sir.compiler;

import static sir.compiler.Value.Type.*;

public class Value {
	enum Type {
		INTEGER, FLOAT, STRING, BOOL, NULL,
	}

	private String sym;
	private Type type;

	private Value() {
		this.sym = null;
		this.type = NULL;
	}

	private Value(String sym) {
		this.sym = sym;
	}

	public static Value Number(String sym) {
		Value num = new Value(sym);
		if (sym.contains(".")) num.type = FLOAT;
		else num.type = INTEGER;
		return num;
	}

	public static Value String(String sym) {
		Value value = new Value(sym);
		value.type = STRING;
		return value;
	}

	public static Value BOOL(String sym) {
		Value value = new Value(sym);
		value.type = BOOL;
		return value;
	}

	public static Value Null() {
		return new Value();
	}

	public boolean bool_value() {
		return Boolean.valueOf(sym);
	}

	public long int_value() {
		return Long.valueOf(sym);
	}

	public double float_value() {
		return Double.valueOf(sym);
	}

	public String str_value() {
		return sym;
	}

	public Value.Type type_of() {
		return type;
	}

}

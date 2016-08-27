package sir.compiler;


public class Token {
	enum Type {
		KEYWORD, IDENTIFIER, NUMBER, STRING, BOOL, NULL, EOF,
		L_BRACE, R_BRACE, L_PAREN, R_PAREN, L_BRACKET, R_BRACKET,
		DOT, COMMA, COLON, SEMICOLON,
		NEQUALS, EQUALS, GEQ, LEQ, GT, LT, PLUSPLUS, MINUSMINUS,
		STRICT_ASSIGN, ASSIGN, PLUS, MINUS, TIMES, DIV, MOD,
		NOT, AND, OR, B_AND, B_OR, B_NOT, B_XOR, R_SHIFT, L_SHIFT;
	}

	private final String sym_val;
	private final Type type;
	private final int line;

	private Token(int lineno, String value, Type t) {
		sym_val = value;
		type = t;
		line = lineno;
	}

	public static Token Null(int lineno) {
		return new Token(lineno, "null", Type.NULL);
	}

	public static Token EOF(int lineno) {
		return new Token(lineno, "null", Type.EOF);
	}

	public static Token Keyword(String name, int lineno) {
		return new Token(lineno, name, Type.KEYWORD);
	}

	public static Token Identifier(String name, int lineno) {
		return new Token(lineno, name, Type.IDENTIFIER);
	}

	public static Token Number(String value, int lineno) {
		return new Token(lineno, value, Type.NUMBER);
	}

	public static Token String(String value, int lineno) {
		return new Token(lineno, value, Type.STRING);
	}

	public static Token Bool(String value, int lineno) {
		return new Token(lineno, value, Type.BOOL);
	}

	public static Token Operator(String op, int lineno) {
		if ("=".equals(op)) return new Token(lineno, op, Type.STRICT_ASSIGN);
		if ("+".equals(op)) return new Token(lineno, op, Type.PLUS);
		if ("-".equals(op)) return new Token(lineno, op, Type.MINUS);
		if ("*".equals(op)) return new Token(lineno, op, Type.TIMES);
		if ("/".equals(op)) return new Token(lineno, op, Type.DIV);
		if ("%".equals(op)) return new Token(lineno, op, Type.MOD);
		if ("++".equals(op)) return new Token(lineno, op, Type.PLUSPLUS);
		if ("--".equals(op)) return new Token(lineno, op, Type.MINUSMINUS);
		if (">>".equals(op)) return new Token(lineno, op, Type.R_SHIFT);
		if ("<<".equals(op)) return new Token(lineno, op, Type.L_SHIFT);

		if ("==".equals(op)) return new Token(lineno, op, Type.EQUALS);
		if ("!=".equals(op)) return new Token(lineno, op, Type.NEQUALS);
		if (">=".equals(op)) return new Token(lineno, op, Type.GEQ);
		if ("<=".equals(op)) return new Token(lineno, op, Type.LEQ);
		if (">".equals(op)) return new Token(lineno, op, Type.GT);
		if ("<".equals(op)) return new Token(lineno, op, Type.LT);

		if ("+=".equals(op)) return new Token(lineno, op, Type.ASSIGN);
		if ("-=".equals(op)) return new Token(lineno, op, Type.ASSIGN);
		if ("*=".equals(op)) return new Token(lineno, op, Type.ASSIGN);
		if ("/=".equals(op)) return new Token(lineno, op, Type.ASSIGN);
		if ("%=".equals(op)) return new Token(lineno, op, Type.ASSIGN);
		if ("&=".equals(op)) return new Token(lineno, op, Type.ASSIGN);
		if ("|=".equals(op)) return new Token(lineno, op, Type.ASSIGN);
		if ("^=".equals(op)) return new Token(lineno, op, Type.ASSIGN);
		if (">>=".equals(op)) return new Token(lineno, op, Type.ASSIGN);
		if ("<<=".equals(op)) return new Token(lineno, op, Type.ASSIGN);

		if ("!".equals(op)) return new Token(lineno, op, Type.NOT);
		if ("&&".equals(op)) return new Token(lineno, op, Type.AND);
		if ("||".equals(op)) return new Token(lineno, op, Type.OR);

		if ("~".equals(op)) return new Token(lineno, op, Type.B_NOT);
		if ("&".equals(op)) return new Token(lineno, op, Type.B_AND);
		if ("|".equals(op)) return new Token(lineno, op, Type.B_OR);
		if ("^".equals(op)) return new Token(lineno, op, Type.B_XOR);

		return Null(lineno);
	}

	public static Token Punctuation(char value, int lineno) {
		if ('.' == value) return new Token(lineno, "" + value, Type.DOT);
		if (',' == value) return new Token(lineno, "" + value, Type.COMMA);
		if ('{' == value) return new Token(lineno, "" + value, Type.L_BRACE);
		if ('}' == value) return new Token(lineno, "" + value, Type.R_BRACE);
		if ('[' == value) return new Token(lineno, "" + value, Type.L_BRACKET);
		if (']' == value) return new Token(lineno, "" + value, Type.R_BRACKET);
		if ('(' == value) return new Token(lineno, "" + value, Type.L_PAREN);
		if (')' == value) return new Token(lineno, "" + value, Type.R_PAREN);
		if (':' == value) return new Token(lineno, "" + value, Type.COLON);
		if (';' == value) return new Token(lineno, "" + value, Type.SEMICOLON);
		return Null(lineno);
	}

	public String str_value() { return sym_val; }

	public Type type_of() {
		return type;
	}

	public int line_of() {
		return line;
	}

	private String repr() {
		switch (type) {
			case KEYWORD:   return sym_val;
			case NUMBER:    return sym_val;
			case BOOL:      return sym_val;
			case NULL:      return sym_val;
			case EOF:       return sym_val;
			case STRING: {
				String repr = "";
				for (int i = 0; i < sym_val.length(); i++)
					switch (sym_val.charAt(i)) {
						case '\n':  repr += "\\n";  break;
						case '\t':  repr += "\\t";  break;
						case '\r':  repr += "\\r";  break;
						case '\b':  repr += "\\b";  break;
						default:    repr += sym_val.charAt(i);
					}
				return "\"" + repr + "\"";
			}
			default:
				return "'" + sym_val + "'";
		}
	}

	public String toString() {
		return line + ":(" + type + ", " + repr() + ")";
	}
}

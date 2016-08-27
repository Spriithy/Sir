package sir.compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
	final String[] keywords = new String[]{
			"routine", "make", "let", "get", "register", "finalize",
			"var", "undef", "goto", "if", "else", "do", "while", "for", "unless",
			"return", "enum", "struct", "static", "extern"
	};

	final String[] operators = new String[]{
			"+", "-", "*", "/", "%", "!", "&&", "||", "^", "=", ">", "<", "++",
			"--", "**", "+=", "-=", "*=", "/=", "&=", "|=", "^=", "~=", "<=",
			">=", "!=", "==", ">>", "<<", "~", "|", "&", ">>=", "<<="
	};

	private enum CommentMode {INLINE, MULTILINE}

	private String input;
	private int at, lineno;

	private char curr, next;
	private ArrayList<Token> tokens;

	private final boolean verbose;

	public Lexer(String path, boolean verbose) {
		this.verbose = verbose;
		try {
			this.tokens = new ArrayList<>();
			this.input = getFileContent(path) + " ";
			this.lineno = 1;
			this.at = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean next() {
		if (curr == '\n') lineno++;

		if (at < input.length()) {
			curr = next;
			next = next_char();
			return true;
		}
		return false;
	}

	private char next_char() {
		if (at > input.length())
			return '\0';
		return input.charAt(at++);
	}

	public void lex() {
		while (next())
			process();
		tokens.add(Token.EOF(lineno));
		if (verbose)
			System.out.println(tokens.toString().substring(1).replace("), ", ")\n").replace("]", ""));
	}

	public ArrayList<Token> production() {
		return tokens;
	}

	private void process() {
		if (verbose)
			System.out.println(lineno + ": curr='" + repr(curr) + "' \tnext='" + repr(next) + "'");

		if ((curr + "" + next).equals("//"))        comment(CommentMode.INLINE);
		else if ((curr + "" + next).equals("/*"))   comment(CommentMode.MULTILINE);
		else if (isWhitespace(curr))                return;
		else if (isIdStart(curr))                   identifier();
		else if (isPunctuation(curr))               punctuation();
		else if (curr == '"')                       string();
		else if (isNumStart(curr))                  number();
		else if (isOperator(curr + "" + next))      operator2();
		else if (isOperator(curr + ""))             operator1();
		else { /* TODO ERROR */ }
	}

	private void number() {
		boolean decimal = false;
		String num = "" + curr;
		while (isNumPart(next)) {
			if (next == '.' && !decimal) decimal = true;
			else if (next == '.') { /* TODO ERROR */ }
			num += next;
			next();
		}
		tokens.add(Token.Number(num, lineno));
	}

	private void operator1() {
		tokens.add(Token.Operator("" + curr, lineno));
	}

	private void operator2() {
		String op2 = "" + curr + next;
		next();
		if (isOperator(op2 + next)) {
			tokens.add(Token.Operator(op2 + next, lineno));
			next();
		} else tokens.add(Token.Operator(op2, lineno));
	}

	private void string() {
		boolean escape = false;
		String str = "";
		next();
		while (curr != '"') {
			if (curr == '\\') escape = !escape;

			if (escape) {
				switch (next) {
					case 'n':
						str += "\n";
						break;
					case 't':
						str += "\t";
						break;
					case 'r':
						str += "\r";
						break;
					case '0':
						str += "\0";
						break;
					case 'b':
						str += "\b";
						break;
					case '"':
						str += "\"";
						break;
					default:
						// TODO ERROR
						break;
				}
				next();
				escape = false;
			} else str += curr;
			next();
		}
		tokens.add(Token.String(str, lineno));
	}

	private void punctuation() {
		tokens.add(Token.Punctuation(curr, lineno));
	}

	private void identifier() {
		String identifier = "" + curr;
		while (isIdPart(next)) {
			identifier += next;
			next();
		}

		if (isKeyword(identifier))
			tokens.add(Token.Keyword(identifier, lineno));
		else if (isReservedWord(identifier))
			switch (identifier) {
				case "null":
					tokens.add(Token.Null(lineno));
					break;
				case "true":
					tokens.add(Token.Bool("true", lineno));
					break;
				case "false":
					tokens.add(Token.Bool("false", lineno));
			}
		else
			tokens.add(Token.Identifier(identifier, lineno));
	}

	private void comment(final CommentMode mode) {
		switch (mode) {
			case INLINE:
				while (curr != '\n') next();
				break;
			case MULTILINE:
				while (!(curr + "" + next).equals("*/")) next();
				break;
		}
		if (mode.equals(CommentMode.MULTILINE)) next();
	}

	private boolean isNumStart(final char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isNumPart(final char c) {
		return c == '.' || isNumStart(c);
	}


	private boolean isIdStart(final char c) {
		return c == '_' || c == '$' || c == '%' || c == '#' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean isIdPart(final char c) {
		return c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
	}

	private boolean isKeyword(final String sym) {
		for (String keyword : keywords)
			if (sym.equals(keyword)) return true;
		return false;
	}

	private boolean isReservedWord(final String sym) {
		for (String word : new String[]{"null", "true", "false"})
			if (word.equals(sym)) return true;
		return false;
	}

	private boolean isPunctuation(final char sym) {
		for (char p : new char[]{'.', ',', '{', '}', ':', '(', ')', '[', ']', ';'})
			if (sym == p) return true;
		return false;
	}

	private boolean isOperator(final String sym) {
		for (String op : operators)
			if (sym.equals(op)) return true;
		return false;
	}

	private boolean isWhitespace(final char sym) {
		return sym == '\n' || sym == '\t' || sym == '\r' || sym == ' ';
	}

	private String repr(final char c) {
		switch (c) {
			case '\n':
				return "\\n";
			case '\t':
				return "\\t";
			case '\r':
				return "\\r";
			case '\b':
				return "\\b";
			case '\0':
				return "\\0";
			default:
				return "" + c;
		}
	}

	private String getFileContent(String path) throws java.io.IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

		} finally {
			reader.close();
		}
		return stringBuilder.toString();
	}
}

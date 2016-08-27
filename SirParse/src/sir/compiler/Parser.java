package sir.compiler;

import java.util.ArrayList;

import static sir.compiler.Token.Type.*;

public class Parser {
	private String currentStmt;

	private ArrayList<Token> tokens;
	private Token token;
	private int at;

	private int lineno;
	private boolean verbose;
	private String report_header;

	public Parser(ArrayList<Token> tokens, String file, boolean verbose) {
		this.verbose = verbose;
		this.currentStmt = "";
		this.tokens = tokens;
		this.report_header = file + ":";
		this.lineno = 0;
		this.at = 0;
	}

	private Token next() {
		if (at >= tokens.size())
			return Token.EOF(lineno);
		return tokens.get(at++);
	}

	public void parse() {
		token = next();
		try {
			while (top_level_check())
				;
		} catch (SyntaxError e) {
			e.printStackTrace();
			System.exit(1);
		}
		match(EOF);
	}

	private boolean top_level_check() throws SyntaxError {
		if (!matchKeyword("static")) {
			if (!matchKeyword("extern")) {
				if (global_var("classic")) return true;
				if (routine_declaration("classic")) return true;
			}
			if (global_var("extern")) return true;
			if (routine_declaration("extern")) return true;
		}
		if (global_var("static")) return true;
		if (routine_declaration("static")) return true;
		return false;
	}

	private boolean global_var(String mode) throws SyntaxError {
		switch (mode) {
			case "classic":
				if (!matchKeyword("var")) return false;
				if (!var_declaration_list())
					throw new SyntaxError("Expected var declaration list");
				if (!semicolon())
					throw new SyntaxError("Expected ';' to terminate statement");
				break;
			case "static":
				if (!matchKeyword("var")) return false;
				if (!var_declaration_list())
					throw new SyntaxError("Expected var declaration list");
				if (!semicolon())
					throw new SyntaxError("Expected ';' to terminate statement");
				break;
			case "extern":
				if (!matchKeyword("var")) return false;
				if (!id_list())
					throw new SyntaxError("Expected id list");
				if (!semicolon())
					throw new SyntaxError("Expected ';' to terminate statement");
				break;
			default:
				return false;
		}
		if (verbose)
			System.out.println(report_header() + "Matched " + mode + " global variable declaration");
		return true;
	}


	private String report_header() {
		return report_header + lineno + "> ";
	}

	private boolean routine_declaration(String mode) throws SyntaxError {
		switch (mode) {
			case "classic":
				if (!routine_header()) return false;
				if (!block())
					throw new SyntaxError("Expected statement block to match routine declaration on line " + token.line_of() + " instead of token " + token);
				break;
			case "static":
				if (!routine_header()) return false;
				if (!block())
					throw new SyntaxError("Expected statement block to match static routine declaration on line " + token.line_of() + " instead of token " + token);
				break;
			case "extern":
				if (!routine_header()) return false;
				if (!semicolon())
					throw new SyntaxError("Expected semicolon ';' to end extern routine declaration on line" + token.line_of() + " instead of token " + token);
				break;
			default:
				return false;
		}
		if (verbose)
			System.out.println(report_header() + "Matched " + mode + " routine declaration");
		return true;
	}

	private boolean routine_header() throws SyntaxError {
		if (!matchKeyword("routine")) return false;
		if (!identifier())
			throw new SyntaxError("Expected routine name identifier to match routine header on line " + token.line_of() + " instead of token " + token);
		if (!routine_arg_list())
			throw new SyntaxError("Expected argument list (or empty '()') to match routine header on line " + token.line_of() + " instead of token " + token);
		return true;
	}

	private boolean assign() throws SyntaxError {
		if (!identifier()) return false;
		if (!match(STRICT_ASSIGN)) throw new SyntaxError("Expected '=' token");
		if (!expression()) throw new SyntaxError("Expected expression");
		return true;
	}

	private boolean assign_list() throws SyntaxError {
		if (!assign()) return false;
		while (match(COMMA))
			if (!assign())
				throw new SyntaxError("Expected identifier in ID-LIST after matched token ',' on line " + token.line_of());
		return true;
	}

	private boolean routine_arg_list() throws SyntaxError {
		if (!match(L_PAREN)) return false;
		if (!match(R_PAREN)) {
			if (!id_list())
				throw new SyntaxError("Expected ID-LIST or empty parentheses pair on line " + token.line_of());
			return match(R_PAREN);
		}
		return true;
	}

	private boolean block() throws SyntaxError {
		if (!match(L_BRACE)) return false;
		statement_list();
		if (!match(R_BRACE))
			throw new SyntaxError("Expected closing curly brace '}' to match end of block declaration on line " + token.line_of());
		return true;
	}

	private boolean statement_list() {
		while (statement())
			;
		return true;
	}

	private boolean statement() {
		if (!semicolon()) {
			if (!label_declaration()) {
				return false;
			}
			return true;
		}
		return true;
	}

	private boolean label_declaration() {
		if (!match(IDENTIFIER)) return false;
		if (!match(COLON)) return false;
		return true;
	}

	private boolean expression() throws SyntaxError {
		return match(NULL) || match(NUMBER) || match(STRING) || match(IDENTIFIER) || match(BOOL);
	}

	private boolean id_list() throws SyntaxError {
		if (!identifier()) return false;
		while (match(COMMA))
			if (!identifier())
				throw new SyntaxError("Expected identifier in ID-LIST after matched token ',' on line " + token.line_of());
		return true;
	}

	private boolean var_declaration_list() throws SyntaxError {
		if (!var_declaration()) return false;
		while (match(COMMA))
			if (!var_declaration())
				throw new SyntaxError("Expected variable declaration in DECL-LIST after matched token ',' on line " + token.line_of());
		return true;
	}

	private boolean var_declaration() throws SyntaxError {
		if (!id_list())
			throw new SyntaxError("Expected one or more identifier(s) to match 'let' statement in global scope on line " + token.line_of());
		if (!match(STRICT_ASSIGN))
			return true;
		if (!expression())
			throw new SyntaxError("Expected constant expression after token '=' to match 'let' statement in global scope on line " + token.line_of());
		return true;
	}

	private boolean identifier() {
		return match(IDENTIFIER);
	}

	private boolean semicolon() {
		return match(SEMICOLON);
	}

	private boolean assign_op() {
		return match(STRICT_ASSIGN) || match(ASSIGN);
	}

	private boolean unary_op() {
		return match(PLUS) || match(MINUS) || match(NOT) || match(B_NOT);
	}

	private boolean match(Token.Type type) {
		if (token.type_of().equals(type)) {
			if (verbose) System.out.println("Matched token " + token);
			currentStmt += token.str_value() + " ";
			token = next();
			return true;
		} else if (verbose)
			System.out.println("Rejected " + token + " (" + type + " would have matched)");
		return false;
	}

	private boolean matchKeyword(String value) {
		if (token.type_of().equals(KEYWORD) && token.str_value().equals(value)) {
			if (verbose) System.out.println("Matched keyword '" + value + "'");
			currentStmt += token.str_value() + " ";
			token = next();
			lineno = token.line_of();
			return true;
		} else if (verbose)
			System.out.println("Rejected " + token + " (" + value + " could have matched)");
		return false;
	}
}

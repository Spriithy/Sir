package sir.compiler;

public class ParseTest {

	public static void main(String[] args) {
		Lexer lexer = new Lexer("SirLex/samples/main.sir", false);
		lexer.lex();
		Parser parser = new Parser(lexer.production(), "SirLex/samples/main.sir", true);
		parser.parse();
	}
}

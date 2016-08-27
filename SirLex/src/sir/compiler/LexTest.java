package sir.compiler;

public class LexTest {

	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));

		Lexer lexer = new Lexer("SirLex/samples/main.sir", true);
		lexer.lex();
	}

}

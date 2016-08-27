package sir.compiler;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ExprParsingTest {
	public static void main(String[] args) {
		String infix = "( ( a == ! b ) || ( c != d ) )";
		System.out.println(postfix(infix));
	}

	private enum Operator {
		ADD(4), SUBTRACT(4), MULTIPLY(3), DIVIDE(3),
		MOD(3), RSHIFT(5), LSHIFT(5), BANG(2), ASSIGN(14), EQUALS(7), NEQUALS(7),
		PLUSPLUS(2), MINUSMINUS(2), PLUSEQ(14), MINUSEQ(14), LT(6), GT(5), LEQ(5), GEQ(5),
		TIMESEQ(14), DIVEQ(14), MODEQ(14), ANDEQ(14), OREQ(14), XOREQ(14),
		RSHEQ(14), LSHEQ(14), B_OR(10), B_AND(8), B_XOR(9), AND(11), OR(12);
		final int precedence;

		Operator(int p) {
			precedence = p;
		}
	}

	private static Map<String, Operator> ops = new HashMap<String, Operator>() {{
		put("+", Operator.ADD);
		put("-", Operator.SUBTRACT);
		put("*", Operator.MULTIPLY);
		put("/", Operator.DIVIDE);
		put("%", Operator.MOD);
		put("&", Operator.B_AND);
		put("&&", Operator.AND);
		put("|", Operator.B_OR);
		put("||", Operator.OR);
		put("^", Operator.B_XOR);
		put("!", Operator.BANG);
		put("=", Operator.ASSIGN);
		put("++", Operator.PLUSPLUS);
		put("--", Operator.MINUSMINUS);
		put(">>", Operator.RSHIFT);
		put("<<", Operator.LSHIFT);
		put(">>=", Operator.RSHEQ);
		put("<<=", Operator.LSHEQ);
		put("+=", Operator.PLUSEQ);
		put("-=", Operator.MINUSEQ);
		put("*=", Operator.TIMESEQ);
		put("/=", Operator.DIVEQ);
		put("%=", Operator.MODEQ);
		put("&=", Operator.ANDEQ);
		put("|=", Operator.OREQ);
		put("^=", Operator.XOREQ);
		put("==", Operator.EQUALS);
		put("!=", Operator.NEQUALS);
		put("<>", Operator.NEQUALS);
		put(">", Operator.GT);
		put("<", Operator.LT);
		put(">=", Operator.GEQ);
		put("<=", Operator.LEQ);
	}};

	private static boolean isHigerPrec(String op, String sub) {
		return (ops.containsKey(sub) && ops.get(sub).precedence >= ops.get(op).precedence);
	}

	public static String postfix(String infix) {
		StringBuilder output = new StringBuilder();
		Deque<String> stack = new LinkedList<>();

		for (String token : infix.split("\\s")) {
			// operator
			if (ops.containsKey(token)) {
				while (!stack.isEmpty() && isHigerPrec(token, stack.peek()))
					output.append(stack.pop()).append(' ');
				stack.push(token);

				// left parenthesis
			} else if (token.equals("(")) {
				stack.push(token);

				// right parenthesis
			} else if (token.equals(")")) {
				while (!stack.peek().equals("("))
					output.append(stack.pop()).append(' ');
				stack.pop();

				// digit
			} else {
				output.append(token).append(' ');
			}
		}

		while (!stack.isEmpty())
			output.append(stack.pop()).append(' ');

		return output.toString();
	}

}

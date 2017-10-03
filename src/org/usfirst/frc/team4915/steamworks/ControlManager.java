package org.usfirst.frc.team4915.steamworks;

import java.util.Map;
import java.util.function.DoubleSupplier;

public class ControlManager {
	
	private Logger m_logger;
	private Map<String,String> m_expressionOutputs; // Expression outputs are mathematical expressions, meant to be evaluated, that have (mathematical) variables from m_controllerInputs
	private static Map<String,DoubleSupplier> m_controllerInputs; // We use DoubleSupplier here to pass around functions like they are first-class
	
	
	public ControlManager(Logger logger) {
		m_logger = logger;
	}
	
	public void registerExpressionOutput(String name, String defualtExpression) {
		m_expressionOutputs.put(name, defualtExpression); // This just replaces/adds the key, so the registerExpressionOutput method can be called when a source is changed or added
	}
	
	public void registerControllerInput(String name, DoubleSupplier method) {
		m_controllerInputs.put(name,method);
	}
	
	public double getExpressionOutput(String name) {
		String expression = m_expressionOutputs.get(name);
		if (expression == null) {
			m_logger.error("No expression exists called " + name);
			return 0.0;
		}
		try {
			return eval(expression);
		} catch (Exception e) {
			m_logger.exception(e, true);
			return 0.0;
		}
	}
	
	// Recursive descent parser for mathematical expressions, modified for variables (See this Stack Overflow answer: https://stackoverflow.com/a/26227947)
	private static double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else if (ch >= 'A' && ch <- 'Z') { // variables
	            	while (ch >= 'A' && ch <= 'Z') nextChar();
	            	String varName = str.substring(startPos, this.pos);
	                x = parseFactor();
	            	DoubleSupplier method = m_controllerInputs.get(varName);
	            	if (method == null) {
	            		throw new RuntimeException("Unknown variable: " + varName);
	            	}
	            	x = method.getAsDouble();
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
}
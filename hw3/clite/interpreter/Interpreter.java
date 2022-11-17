package clite.interpreter;

import java.util.*;
import java.util.concurrent.locks.Condition;

import clite.ast.Assignment;
import clite.ast.Block;
import clite.ast.Binary;
import clite.ast.Conditional;
import clite.ast.Declarations;
import clite.ast.Declaration;
import clite.ast.Expression;
import clite.ast.Loop;
import clite.ast.Operator;
import clite.ast.Program;
import clite.ast.Statement;
import clite.ast.Type;
import clite.ast.Unary;
import clite.ast.Value;
import clite.ast.Variable;
import clite.ast.Skip;
import clite.parser.Lexer;
import clite.parser.Parser;
import clite.typing.TypeChecker;

public class Interpreter {

	/**
	 * Evaluates a program and returns the resulting state.
	 * @param program
	 * @return
	 */
	public static State meaning( Program program ) {
		State state = initState(program.getDecpart(), new State());
		meaning(program.getBody(), state);
		return state;
	}

	/**
	 * Evaluates a block and returns the resulting state.
	 * @param body
	 * @param s
	 * @return
	 */
	public static void meaning(Block body, State s) {
		State state = initState(body.getDeclarations(), new State(s));

		for(Statement stmt : body.getStatements()) {
			statementMeaning(stmt, state);
		}
	}

	/**
	 * Evaluates a statement and returns the resulting state.
	 * @param stmt
	 * @param state
	 * @return
	 */
	private static void statementMeaning(Statement stmt, State state) {
		if(stmt instanceof Skip) {
			//Do nothing
		}
		else if(stmt instanceof Block) {
			meaning((Block)stmt, state);
		}
		else if(stmt instanceof Assignment) {
			assignState((Assignment)stmt, state);
		}
		else if(stmt instanceof Conditional) {
			Conditional cond = (Conditional)stmt;
			if((boolean)evaluateExpression(cond.getTest(), state).getValue()) {
				statementMeaning(cond.getThenbranch(), state);
			}
			else {
				statementMeaning(cond.getElsebranch(), state);
			}
		}
		else if(stmt instanceof Loop) {
			Loop loop = (Loop)stmt;
			while((boolean)evaluateExpression(loop.getTest(), state).getValue()) {
				statementMeaning(loop.getBody(), state);
			}
		}
	}

	/**
	 * Returns an initialized a state.
	 * @param declarations
	 * @param s
	 * @return
	 */
	private static State initState(Declarations declarations, State state) {
		for(Declaration dec : declarations) {
			state.init(dec.getV());
		}
		return state;
	}

	/**
	 * Assigns a value to the identified variable in the state.
	 * @param a
	 * @param state
	 * @return
	 */
	private static void assignState(Assignment a, State state) {
		state.put(a.getTarget(), evaluateExpression(a.getSource(), state));
	}

	/**
	 * Evaluates an expression and returns the value.
	 * @param e
	 * @param s
	 * @return
	 */
	private static Value evaluateExpression(Expression e, State s) {
		if(e instanceof Value) {
			Value<?> temp = (Value<?>) e;
			return temp;
		}
		else if(e instanceof Variable) {
			Value<?> temp = (Value<?>) s.get((Variable)e);
			return temp;
		}
		else if(e instanceof Binary) {
			Binary b = (Binary)e;
			return applyBinary(b.getOp(), evaluateExpression(b.getTerm1(), s), evaluateExpression(b.getTerm2(), s));
		}
		else if(e instanceof Unary) {
			Unary u = (Unary)e;
			return applyUnary(u.getOp(), evaluateExpression(u.getTerm(), s));
		}
		return null;
	}

	/**
	 * Evaluates and returns the value of a unary expression.
	 * @param o
	 * @param v1
	 * @return
	 */
	private static Value<?> applyUnary(Operator o, Value<?> v1) {
		if(o.is(Operator.NOT)) {
			return new Value(!((boolean)v1.getValue()));
		}
		else if(o.is(Operator.NEG)) {
			return new Value(-((int)v1.getValue()));
		}
		return null;
	}

	/**
	 * Evaluates and returns the value of a binary expression.
	 * @param o
	 * @param term1
	 * @param term2
	 * @return
	 */
	private static Value<?> applyBinary(Operator o, Value<?> v1, Value<?> v2) {
		try {
			if(o.is(Operator.PLUS)) {
				return new Value((int)v1.getValue() + (int)v2.getValue());
			} 
			else if(o.is(Operator.MINUS)) {
				return new Value((int)v1.getValue() - (int)v2.getValue());
			}
			else if(o.is(Operator.TIMES)) {
				return new Value((int)v1.getValue() * (int)v2.getValue());
			}
			else if(o.is(Operator.DIV)) {
				return new Value(Math.floor(Math.abs((int)v1.getValue() / (int)v2.getValue()) * getSign((int)v1.getValue() / (int)v2.getValue())));
			}
			else if(o.is(Operator.AND)) {
				return new Value((boolean)v1.getValue() && (boolean)v2.getValue());
			}
			else if(o.is(Operator.OR)) {
				return new Value((boolean)v1.getValue() || (boolean)v2.getValue());
			}
			else if(o.is(Operator.LT)) {
				return new Value((int)v1.getValue() < (int)v2.getValue());
			}
			else if(o.is(Operator.LE)) {
				return new Value((int)v1.getValue() <= (int)v2.getValue());
			}
			else if(o.is(Operator.EQ)) {
				return new Value((int)v1.getValue() == (int)v2.getValue());
			}
			else if(o.is(Operator.NE)) {
				return new Value((int)v1.getValue() != (int)v2.getValue());
			} 
			else if(o.is(Operator.GT)) {
				return new Value((int)v1.getValue() > (int)v2.getValue());
			}
			else if(o.is(Operator.GE)) {
				return new Value((int)v1.getValue() >= (int)v2.getValue());
			}
			else {
				return null;
			}
		}
		catch(ClassCastException e) {
			if(o.is(Operator.EQ)) {
				return new Value((boolean)v1.getValue() == (boolean)v2.getValue());
			}
			else if(o.is(Operator.NE)) {
				return new Value((boolean)v1.getValue() != (boolean)v2.getValue());
			}
		}
		return null;
	}

	/**
	 * A helper function to return the sign of a value;
	 * @param val
	 * @return
	 */
	private static int getSign(double val) {
		return val / Math.abs(val);
	}
	
	public static void main(String[] args ) {
		Parser parser  = new Parser(new Lexer(args[0]) );
		Program program = parser.program();
		if( !TypeChecker.isValid( program ) ) {
			System.out.println("Program is not valid.");
		} else {
			System.out.println( meaning( program ) );
		}
	}
	
}

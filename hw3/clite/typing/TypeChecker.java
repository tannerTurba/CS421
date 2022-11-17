package clite.typing;

import java.util.*;

import clite.ast.Assignment;
import clite.ast.Binary;
import clite.ast.Block;
import clite.ast.Conditional;
import clite.ast.Declaration;
import clite.ast.Declarations;
import clite.ast.Expression;
import clite.ast.Loop;
import clite.ast.Operator;
import clite.ast.Program;
import clite.ast.Skip;
import clite.ast.Statement;
import clite.ast.Type;
import clite.ast.Unary;
import clite.ast.Value;
import clite.ast.Variable;
import clite.parser.Lexer;
import clite.parser.Parser;

public class TypeChecker {

	/**
	 * Program = Declarations decPart; Block body;
	 * @param p
	 * @return
	 */
	public static boolean isValid(Program p) {
		TypeMap map = getTypeMap(null, p.getDecpart());
		return areDeclarationsValid(p.getDecpart(), map) && isBlockValid(p.getBody(), map);
	} 

	/**
	 * Creates and returns a new TypeMap that will add to an existing map, 
	 * or create an entirely new map.
	 * @param oldMap
	 * @param newDecs
	 * @return
	 */
	private static TypeMap getTypeMap(TypeMap oldMap, Declarations newDecs) {
		TypeMap newMap;
		if(oldMap == null) {
			newMap = new TypeMap();
		}
		else {
			newMap = new TypeMap(oldMap);
		}

		for(Declaration dec : newDecs) {
			newMap.put(dec.getV(), dec.getT());
		}
		return newMap;
	}

	/**
	 * Validate that each declaration contains a valid type and a valid variable name.
	 * @param decpart
	 * @param tMap
	 * @return
	 */
	private static boolean areDeclarationsValid(Declarations decpart, TypeMap tMap) {
		for(Declaration dec : decpart) {
			if(!(isTypeValid(dec.getT()) && isVariableValid((Variable)dec.getV(), tMap))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks validity of a block statement.
	 * @param body
	 * @param tMap
	 * @return
	 */
	private static boolean isBlockValid(Block body, TypeMap tMap) {
		//Verify all declared variables are unique.
		Declarations decls = body.getDeclarations();
		Set<Declaration> setOfDecls = new HashSet<Declaration>(decls);
		if(setOfDecls.size() < decls.size()) {
			return false;
		}

		//Check validity of declarations and statements.
		TypeMap newMap = getTypeMap(tMap, body.getDeclarations());
		return areDeclarationsValid(body.getDeclarations(), newMap) && areStatementsValid(body.getStatements(), newMap);
	}

	/**
	 * Validates that the type is either an INT or a BOOL.
	 * @param t
	 * @return
	 */
	private static boolean isTypeValid(Type t) {
		return t == Type.INT || t == Type.BOOL;
	}

	/**
	 * Validates that each statement from a list of statements is valid.
	 * @param statements
	 * @param tMap
	 * @return
	 */
	private static boolean areStatementsValid(List<Statement> statements, TypeMap tMap) {
		for(Statement stmt : statements) {
			if(!isStatementValid(stmt, tMap)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines if the given statement is valid.
	 * @param stmt
	 * @param tMap
	 * @return
	 */
	private static boolean isStatementValid(Statement stmt, TypeMap tMap) {
		if(stmt instanceof Skip) {
			return isSkipValid((Skip)stmt);
		}
		else if(stmt instanceof Block) {
			return isBlockValid((Block)stmt, tMap);
		}
		else if(stmt instanceof Assignment) {
			return isAssignmentValid((Assignment)stmt, tMap);
		}
		else if(stmt instanceof Conditional) {
			return isConditionalValid((Conditional)stmt, tMap);
		}
		else if(stmt instanceof Loop) {
			return isLoopValid((Loop)stmt, tMap);
		}
		else {
			return false;
		}
	}

	/**
	 * Confirms that a Skip value is in fact a Skip.
	 * @param s
	 * @return
	 */
	private static boolean isSkipValid(Skip s) {
		return s.toString().equals(";");
	}

	/**
	 * Determines the validity of an assignment by making sure both sides are valid 
	 * and are of the same tipe.
	 * @param a
	 * @param tMap
	 * @return
	 */
	private static boolean isAssignmentValid(Assignment a, TypeMap tMap) {
		return isVariableValid((Variable)a.getTarget(), tMap) &&
			   isExpressionValid((Expression)a.getSource(), tMap) && 
			   typeOf(a.getTarget(), tMap) == typeOf(a.getSource(), tMap);
	}

	/**
	 * Determines if a conditional statemetn is valid if all three parts are valid 
	 * and the test evaluates to a BOOL value.
	 * @param c
	 * @param tMap
	 * @return
	 */
	private static boolean isConditionalValid(Conditional c, TypeMap tMap) {
		return isExpressionValid(c.getTest(), tMap) && 
			   typeOf(c.getTest(), tMap) == Type.BOOL &&
			   isStatementValid(c.getThenbranch(), tMap) && 
			   isStatementValid(c.getElsebranch(), tMap);
	}

	/**
	 * Determines if a loop is valid depending on the validty of
	 * the test and body. The test must also evaluate to a BOOL value.
	 * @param l
	 * @param tMap
	 * @return
	 */
	private static boolean isLoopValid(Loop l, TypeMap tMap) {
		return isExpressionValid(l.getTest(), tMap) && 
			   isStatementValid(l.getBody(), tMap) &&
			   typeOf(l.getTest(), tMap) == Type.BOOL;
	}

	/**
	 * Determines if an experssion is valid based on the validity of its value, or
	 * subexpressions. 
	 * @param e
	 * @param tMap
	 * @return
	 */
	private static boolean isExpressionValid(Expression e, TypeMap tMap) {
		if(e instanceof Variable) {
			return isVariableValid((Variable)e, tMap);
		}
		else if(e instanceof Value) {
			return isValueValid((Value)e);
		}
		else if(e instanceof Binary) {
			Binary b = (Binary) e;
			return isBinaryValid(b, tMap) &&
				   (b.getOp().is(Operator.EQ) || b.getOp().is(Operator.NE)) && typeOf(b.getTerm1(), tMap) == Type.BOOL && typeOf(b.getTerm2(), tMap) == Type.BOOL ||
				   b.getOp().BooleanOp() && typeOf(b.getTerm1(), tMap) == Type.BOOL && typeOf(b.getTerm2(), tMap) == Type.BOOL ||
				   (b.getOp().ArithmeticOp() || b.getOp().RelationalOp()) && typeOf(b.getTerm1(), tMap) == Type.INT && typeOf(b.getTerm2(), tMap) == Type.INT;
		}
		else if(e instanceof Unary) {
			Unary u = (Unary) e;
			return u.getOp().is(Operator.MINUS) && isExpressionValid(u.getTerm(), tMap) && typeOf(u.getTerm(), tMap) == Type.INT || 
				   u.getOp().is(Operator.NOT) && isExpressionValid(u.getTerm(), tMap) && typeOf(u.getTerm(), tMap) == Type.BOOL;
		}
		else {
			return false;
		}
	}

	/**
	 * Enforce value constraints such that bools are either true or false
	 * and ints are in the range [-2147483648, 2147483647].
	 * @param v
	 * @return
	 */
	private static boolean isValueValid(Value<?> v) {
		switch(v.typeOf()) {
			case BOOL: return ((boolean) v.getValue()) == true || ((boolean) v.getValue()) == false;
			case INT: return (-2147483648 <= (int)v.getValue()) && ((int)v.getValue() <= 2147483647);
			default: return false;
		}
	}

	/**
	 * Determines if a binary value is valid based on the validity of both terms and the first operator.
	 * @param b
	 * @return
	 */
	private static boolean isBinaryValid(Binary b, TypeMap tMap) {
		return isBinaryOp(b.getOp()) && isExpressionValid(b.getTerm1(), tMap) && isExpressionValid(b.getTerm2(), tMap);
	}

	/**
	 * Determines if an operator is a booleanOp, arithmeticOp, or relationalOp.
	 * @param o
	 * @return
	 */
	private static boolean isBinaryOp(Operator o) {
		return o.BooleanOp() || o.ArithmeticOp() || o.RelationalOp();
	}

	/**
	 * Referenced variable has been previously declared and is in scope.
	 * @param v
	 * @param tMap
	 * @return
	 */
	private static boolean isVariableValid(Variable v, TypeMap tMap) {
		return tMap.get(v) != null;
	}

	/**
	 * Returns the type of an expression.
	 * @param e
	 * @param tMap
	 * @return
	 */
	private static <BOOL, INT> Type typeOf(Expression e, TypeMap tMap) {
		if(e instanceof Variable) {
			return tMap.get((Variable)e);
		}
		else if(e instanceof Value) {
			return ((Value<?>)e).typeOf();
		}
		else if(e instanceof Binary) {
			Operator op = ((Binary)e).getOp();
			if(op.ArithmeticOp()) {
				return Type.INT;
			}
			else if(op.BooleanOp() || op.RelationalOp()) {
				return Type.BOOL;
			}
		}
		else if(e instanceof Unary) {
			Operator op = ((Unary)e).getOp();
			if(op.is("-")) {
				return Type.INT;
			}
			else if(op.is("!")) {
				return Type.BOOL;
			}
		}
		return null;
	}	

	public static void main(String args[]) {		
		Parser parser  = new Parser(new Lexer(args[0]) );
		Program prog = parser.program();
		System.out.println(isValid( prog ) );
	}
} 

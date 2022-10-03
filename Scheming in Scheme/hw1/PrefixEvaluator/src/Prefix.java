package PrefixEvaluator.src;
import java.util.*;
import java.io.*;

/**
 * @author Tanner Turba
 * September 24, 2022
 * CS 421 - Programming Language Concepts
 * Prof. Hunt
 * This program evaluates E-Expressions from a text file and prints their results to the console.
 */
public class Prefix{
    private String expression;
    Stack<String> operators = new Stack<>();

    //Start process by getting E-Expressions from a file.
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Please provide a file!");
        }
        else {
            new Prefix(args[0]);
        }
    }

    /**
     * The constructor for the Prefix class. Opens the file and passes each E-Expression
     * for evaluation before printing. 
     * @param file The file containing E-Expressions for evaluation.
     */
    public Prefix(String file) {
        try {
            //Open FileReader and Scanner for parsing the file
            FileReader fReader = new FileReader(new File(file));
            Scanner scanner = new Scanner(fReader);
            
            //Loop through each line and print each evaluation
            while(scanner.hasNext()) {
                expression = scanner.nextLine();
                Scanner expScanner = new Scanner(expression);
                System.out.println(EvaluateExpression(expScanner, new Hashtable<>(), false));
                expScanner.close();
            }

            //Close streams
            scanner.close();
            fReader.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found!");
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Evaluates a block expression iteratively to determine its value. 
     * @param scanner The Scanner containing the E-Expression being evaluated.
     * @param environment The environment that may be applied to the block expression.
     * @return The value of the block expression as a String.
     */
    private String evaluateBlock(Scanner scanner, Map<String, String> environment) {
        //Create local map and copy mappings to it. Create stack for looping.
        Map<String, String> env = new Hashtable<>();
        env.putAll(environment);
        Stack<String> parens = new Stack<>();
        String token;

        //While there are parens left in the stack,
        do {
            //Get the first token of the block.
            token = scanner.next();

            //Add to the stack if it is an opening parens.
            if(token.equals("(")) {
                parens.push(token);
            }
            //Pop from the stack if it is a closing parens.
            else if(token.equals(")")){
                parens.pop();
            }
            //If it is a character, a variable and its definition needs to be mapped.
            else if(Character.isAlphabetic(token.charAt(0))) {
                String id = token;
                String def = EvaluateExpression(scanner, env, true);
                env.put(id, def);
            }
        } while(!parens.isEmpty());

        //After all variables are defined and mapped, evaluate the expression contained in the block, using the local environment.
        String result = EvaluateExpression(scanner, env, false);
        scanner.next();
        return result;
    }
    
    /**
     * Recursively evaluates E-Expressions to determine their value. 
     * @param scanner A Scanner containing the E-Expression to be evaluated.
     * @param environment A Map containing the variable definitons to be used.
     * @param evaluatingBlock A flag used to determine if an expression inside a block is being evaluated.
     * @return The value of the E-Expression as a String.
     */
    public String EvaluateExpression(Scanner scanner, Map<String, String> environment, boolean evaluatingBlock) {
        //Get first token, consume if it is '('
        String token = scanner.next();
        if(token.equals("(")) {
            token = scanner.next();
        }

        //Return when an atomic value is reached. 
        if (Character.isDigit(token.charAt(0)) || token.charAt(0) == '-' && token.length() > 1){
            return token;
        }
        //Return undefined when undefined
        else if (token.equals("undefined")){
            return "undefined";
        }
        //Evaluate and return the environment when block is used.
        else if (token.toLowerCase().equals("block")) {
            return evaluateBlock(scanner, environment);
        }
        //Return the map value or character when an alphabetic character is found.
        else if (Character.isAlphabetic(token.charAt(0))) {
            String value = environment.get(token);
            if(value == null) {
                //Return the token if it is not defined and the expression is inside a block.
                if (evaluatingBlock) {
                    return token;
                }
                //Otherwise return undefined.
                else {
                    return "undefined";
                }
            }
            return value;
        }
        //Catch operators, perform operations, and return.
        else if (!Character.isDigit(token.charAt(0)) && !Character.isAlphabetic(token.charAt(0)) && token.charAt(0) != '(' && token.charAt(0) != ')' && token.length() == 1) {
            return handleOperation(token.charAt(0), scanner, environment);
        }
        return "";
    }

    /**
     * Performs operations on to evaluate the value of a prefix expression.
     * @param operator The operator being used in the expression.
     * @param scanner The Scanner containing the E-Expression being evaluated.
     * @param environment The environment being used in evaluation.
     * @return The result of the operation as a String.
     */
    private String handleOperation(char operator, Scanner scanner, Map<String, String> environment) {
        //Evaluate the first and second expressions to determine their value.
        String e1 = EvaluateExpression(scanner, environment, false);
        String e2 = EvaluateExpression(scanner, environment, false);

        //Try converting to int values, but return "undefined" if they cannot be.
        int exp1, exp2;
        try {
            exp1 = Integer.parseInt(e1);
            exp2 = Integer.parseInt(e2);
        }
        catch(Exception e) {
            scanner.next();
            return "undefined";
        }

        //Consume parens and evaluate expression
        scanner.next();
        try {
            if( operator == '+') {
                return exp1 + exp2 + "";
            } 
            else if (operator == '-') {
                return exp1 - exp2 + "";
            } 
            else if (operator == '*') {
                return exp1 * exp2 + "";
            } 
            else if (operator == '/') {
                return exp1 / exp2 + "";
            } 
            else if (operator == '^') {
                return (int) Math.pow(exp1, exp2) + "";
            } 
            else if (operator == '%') {
                return exp1 % exp2 + "";
            } 
            return "";
        }
        //Catch any logic errors, such as divde by zero.
        catch(ArithmeticException e) {
            return "undefined";
        }
    }
}

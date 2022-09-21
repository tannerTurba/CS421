import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class PrefixEvaluator 
{
    public PrefixEvaluator(String expression) 
    {
        // handleOperation('/', new Scanner(" ( + 9 9 ) 8 )"), new Hashtable<>());
        this.expression = expression;
        Scanner scanner = new Scanner(this.expression);
        System.out.println(EvaluateExpression(scanner, new Hashtable<>(), false));
    }

    private String expression;
    Stack<String> operators = new Stack<>();

    private String evaluateBlock(Scanner scanner) 
    {
        Map<String, String> env = new Hashtable<>();
        Stack<String> parens = new Stack<>();
        String token;
        do {
            token = scanner.next();
            if(token.equals("(")) 
            {
                parens.push(token);
            }
            else if(token.equals(")"))
            {
                parens.pop();
            }
            else if(Character.isAlphabetic(token.charAt(0))) 
            {
                String id = token;
                String def = EvaluateExpression(scanner, env, true);
                env.put(id, def);
            }
        } while(!parens.isEmpty());
        String result = EvaluateExpression(scanner, env, false);
        scanner.next();
        return result;
    }
    
    public String EvaluateExpression(Scanner scanner, Map<String, String> environment, boolean evaluatingBlock) 
    {
        //Get first token, consume if it is '('
        String token = scanner.next();
        if(token.equals("(")) 
        {
            token = scanner.next();
        }

        //Return when an atomic value is reached. 
        if (Character.isDigit(token.charAt(0)) || token.charAt(0) == '-' && token.length() > 1)
        {
            return token;
        }
        //Return undefined when undefined
        else if (token.equals("undefined"))
        {
            return "undefined";
        }
        //Opening Parens mark the beginning of a new expression
        else if (token.equals("(")) 
        {
            return EvaluateExpression(scanner, environment, false);
        }
        //Evaluate the environment when block is used.
        else if (token.toLowerCase().equals("block"))
        {
            return evaluateBlock(scanner);
        }
        //Return the map value or character when an alphabetic character is found.
        else if (Character.isAlphabetic(token.charAt(0))) 
        {
            String value = environment.get(token);
            if(value == null)
            {
                if (evaluatingBlock) 
                {
                    return token;
                }
                else 
                {
                    return "undefined";
                }
            }
            return value;
        }
        //Catch operators
        else if (!Character.isDigit(token.charAt(0)) && !Character.isAlphabetic(token.charAt(0)) && token.charAt(0) != '(' && token.charAt(0) != ')' && token.length() == 1)
        {
            return handleOperation(token.charAt(0), scanner, environment);
        }

        return "";
    }

    private String handleOperation(char operator, Scanner scanner, Map<String, String> environment) 
    {
        String e1 = EvaluateExpression(scanner, environment, false);
        String e2 = EvaluateExpression(scanner, environment, false);

        int exp1, exp2;
        try 
        {
            exp1 = Integer.parseInt(e1);
            exp2 = Integer.parseInt(e2);
        }
        catch(Exception e) 
        {
            scanner.next();
            return "undefined";
        }

        //Consume parens and evaluate expression
        scanner.next();
        if( operator == '+') 
        {
            return exp1 + exp2 + "";
        } 
        else if (operator == '-') 
        {
            return exp1 - exp2 + "";
        } 
        else if (operator == '*') 
        {
            return exp1 * exp2 + "";
        } 
        else if (operator == '/') 
        {
            return exp1 / exp2 + "";
        } 
        else if (operator == '^') 
        {
            return (int) Math.pow(exp1, exp2) + "";
        } 
        else if (operator == '%') 
        {
            return exp1 % exp2 + "";
        } 
        return "";
    }
}

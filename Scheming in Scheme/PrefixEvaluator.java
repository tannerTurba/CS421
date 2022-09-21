import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class PrefixEvaluator 
{
    public PrefixEvaluator(String expression) 
    {
        this.expression = expression;
        Scanner scanner = new Scanner(this.expression);
        System.out.println(rEvaluateExpression(scanner, new Hashtable<>()));
    }

    private String expression;
    Stack<String> operators = new Stack<>();

    private Map<String, String> EvaluateEnvironment(Scanner scanner) 
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
                String def = rEvaluateExpression(scanner, env);
                env.put(id, def);
            }
        } while(!parens.isEmpty());
        return env;
    }

    public String rEvaluateExpression(Scanner scanner, Map<String, String> environment) 
    {
        String token = scanner.next();

        //Return when an atomic value is reached. 
        if (Character.isDigit(token.charAt(0)) || /*Character.isAlphabetic(token.charAt(0)) ||*/ token.charAt(0) == '-' && token.length() > 1)
        {
            return token;
        }
        //Opening Parens mark the beginning of a new expression
        else if (token.equals("(")) 
        {
            return rEvaluateExpression(scanner, environment);
        }
        //Evaluate the environment when block is used.
        else if (token.toLowerCase().equals("block"))
        {
            environment = EvaluateEnvironment(scanner);
            return rEvaluateExpression(scanner, environment);
        }
        //Return the map value or character when an alphabetic character is found.
        else if (Character.isAlphabetic(token.charAt(0))) 
        {
            String value = environment.get(token);
            if(value != null)
            {
                return value;
            }
            return token;
        }
        //Catch operators
        else if (!Character.isDigit(token.charAt(0)) && !Character.isAlphabetic(token.charAt(0)) && token.charAt(0) != '(' && token.charAt(0) != ')' && token.length() == 1)
        {
            char operator = token.charAt(0);
            String e1 = rEvaluateExpression(scanner, environment);
            String e2 = rEvaluateExpression(scanner, environment);

            int exp1, exp2;
            try 
            {
                exp1 = Integer.parseInt(e1);
                exp2 = Integer.parseInt(e2);
            }
            catch(Exception e) 
            {
                return "undefined";
            }

            // Evaluate expression
            if(token.equals(")"))
            {
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
                else if (operator == '!') 
                {
                    return -exp1 + "";
                }
            }
        }
        
        if (token.equals(")") && scanner.hasNext())
        {
            return rEvaluateExpression(scanner, environment);
        }
        return "";
    }
}

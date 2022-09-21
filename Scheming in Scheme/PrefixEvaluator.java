import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

public class PrefixEvaluator 
{


    public PrefixEvaluator(String expression) 
    {
        this.expression = expression;
        Scanner scanner = new Scanner(this.expression);
        System.out.println(rEvaluateExpression(scanner, new Hashtable<>()));
    }


    private class Node 
    {
        private Node left;
        private String data;
        private Node right;

        private Node(Node l, String d, Node r) 
        {
            left = l;
            data = d;
            right = r;
        }
    }

    private Node root;
    private String expression;
    Stack<Dictionary<String, String>> dictionaries = new Stack<>();
    Stack<Node> operands = new Stack<>();
    Stack<String> operators = new Stack<>();

    private Map<String, String> addToEnvironment(Map<String, String> environment, Map<String, String> dictionary) {

        //Copy all values into a single map.
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            environment.put(entry.getKey(), entry.getValue());
        }
        return environment;
    }

    private Map<String, String> EvaluateEnvironment(Scanner scanner) 
    {
        String token = scanner.next();
        Map<String, String> environment = new Hashtable<>();

        //Get a dictionary of environment.
        if (token.equals("(")) 
        {
            Map<String, String> dictionary = EvaluateEnvironment(scanner);
        
            environment = addToEnvironment(environment, dictionary);
            
            //Consume closing parens if a value was added to the environment
            if (dictionary.size() > 0) 
            {
                token = scanner.next();
                if (token.equals("(")) 
                {
                    return addToEnvironment(environment, EvaluateEnvironment(scanner));
                }
            }
            return environment;
        }
        //A character is found, so it needs to be defined.
        else if (Character.isAlphabetic(token.charAt(0)))
        {
            String id = token;
            String definition = rEvaluateExpression(scanner, environment);
            
            Map<String, String> dictionary = new Hashtable<>();
            dictionary.put(id, definition);
            return dictionary;
        }
        else 
        {
            return environment;
        }
    }

    public String rEvaluateExpression(Scanner scanner, Map<String, String> environment) 
    {
        String token = scanner.next();

        //Return when an atomic value is reached. 
        if (Character.isDigit(token.charAt(0)) || /*Character.isAlphabetic(token.charAt(0)) ||*/ token.charAt(0) == '-' && token.length() > 1)
        {
            return token;
        }
        //Evaluate the environment when block is used.
        else if (token.toLowerCase().equals("block"))
        {
            environment = EvaluateEnvironment(scanner);
            return rEvaluateExpression(scanner, environment);
        }
        //Return the map value or undefined when an alphabetic character is found.
        else if (Character.isAlphabetic(token.charAt(0))) 
        {
            try 
            {
                String value = environment.get(token);
                if(!value.equals("null"))
                {
                    return value;
                }
                return "undefined";
            }
            catch(Exception e) 
            {
                return "undefined";
            }
        }
        //Opening Parens mark the beginning of a new expression
        else if (token.equals("(")) 
        {
            return rEvaluateExpression(scanner, environment);
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
            

            //Look at the next token. If it is a ')' the expression is over and can be returned.
            token = scanner.next();
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

    public String evaluateExpressions()
    {
        Scanner scanner = new Scanner(expression);
        return handleExpression(scanner, new Hashtable<>(), false);
    }

    private String handleExpression(Scanner scanner, Dictionary<String, String> dictionary, boolean isDictionaryDef) 
    {
        Node left;
        Node right;
        while(scanner.hasNext()) 
        {
            String token = scanner.next();

            if (token.toLowerCase().equals("block"))
            {
                operands.push(new Node(null, handleBlock(scanner), null));
            }
            else if (!Character.isDigit(token.charAt(0)) && !Character.isAlphabetic(token.charAt(0)) && token.charAt(0) != '(' && token.charAt(0) != ')' && token.length() == 1)
            {
                operators.push(token);
            }
            else if (Character.isDigit(token.charAt(0)) || Character.isAlphabetic(token.charAt(0)) || token.charAt(0) == '-') 
            {
                if (isDictionaryDef) 
                {
                    return token;
                }
                operands.push(new Node(null, token, null));
            }
            else if (token.charAt(0) == ')')
            {
                if (operators.size() > 0 && dictionary.isEmpty()) 
                {
                    right = operands.pop();
                    left = operands.pop();
                    String operator = operators.pop();
                    operands.push(new Node(left, operator, right)); 
                }
                else if (!dictionary.isEmpty()) 
                {
                    String blockExpressionString = operands.pop().data;
                    String translatedBlockExpression = dictionary.get(blockExpressionString);
                    if (translatedBlockExpression != null) 
                    {
                       return translatedBlockExpression; 
                    }
                    return blockExpressionString;
                }
            }
        }
        scanner.close();
        root = operands.pop();
        return "";
    }

    private String handleBlock(Scanner scanner) 
    {
        Stack<String> parens = new Stack<>();
        Dictionary<String, String> dictionary = new Hashtable<>();
        String token = scanner.next();
        boolean needDictionaryDef = false;
        do
        {
            if (token.equals("(")) 
            {
                parens.push("(");
            }
            else if (token.equals(")")) 
            {
                parens.pop();
            }
            else if (Character.isAlphabetic(token.charAt(0)))
            {
                needDictionaryDef = true;
                dictionary.put(token, handleExpression(scanner, dictionary, needDictionaryDef));
                needDictionaryDef = false;
            }
            
            if(!parens.isEmpty())
            {
                token = scanner.next();
            }
            else 
            {
                dictionaries.push(dictionary);
            }
        } while (!parens.isEmpty());

        return handleExpression(scanner, dictionary, needDictionaryDef);  
    }

}

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Stack;

public class PrefixEvaluator 
{
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

    public PrefixEvaluator(String expression) 
    {
        this.expression = expression;
    }

    public String evaluateExpression()
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
        return evaluate(root);
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

    public String evaluate() 
    {
        return evaluate(root);
    }

    private String evaluate(Node root) 
    {
        int left;
        int right;
        int x = 0;
        if(root != null) {
            //recursively obtains the left and right nodes.
            if (Character.isDigit(root.data.charAt(0))) 
            {
                return root.data;
            }
            else 
            {
                try {
                    left = Integer.parseInt(evaluate(root.left));
                    right = Integer.parseInt(evaluate(root.right));  
                } 
                catch(NumberFormatException e) 
                {
                    return "undefined";
                }
            }

            //determines the return value "x" based on certain conditions.
            if(Character.isDigit(root.data.charAt(0)) || (root.data.charAt(0) == '-' && root.data.length() > 1)) 
            {
                x = Integer.parseInt(root.data);
            } 
            else if(Character.isAlphabetic(root.data.charAt(0))) 
            {
                return "undefined";
            }
            else 
            {
                char sign = root.data.charAt(0);
                if( sign == '+') {
                    x = left + right;
                } else if (sign == '-') {
                    x = left - right;
                } else if (sign == '*') {
                    x = left * right;
                } else if (sign == '/') {
                    x = left / right;
                } else if (sign == '^') {
                    x = (int) Math.pow(left, right);
                } else if (sign == '%') {
                    x = left % right;
                } else if (sign == '!') {
                    x = -left ;
                }
            }
        }
        return x + "";
    }

    //The public method to start the recursive process to obtain the infix representation.
    public String toInfix() {
        //return a fully parenthesized infix representation of the expression tree
        //you can assume the tree is not empty
        return toInfix(root);
    }

    //The private method for the recursive process to obtain the infix representation.
    //Does most of the work.
    private String toInfix(Node r) {
        //return a fully parenthesized infix representation
        //of the expression tree rooted at r
        String left;
        String right;
        String infixRepresentation = "";
        if(r != null) {
            //obtains the left and right nodes with recursion.
            left = toInfix(r.left);
            right = toInfix(r.right);

            //determines the infix representation to return, based on certain conditions.
            if(r.left == null) {
                infixRepresentation = left + r.data + right;
            } else if(r.data.equals("!")){
                infixRepresentation = "(- " + left + ")";
            } else {
                infixRepresentation = "(" + left + " " + r.data + " " + right + ")";
            }
        }
        return infixRepresentation;
    }
}

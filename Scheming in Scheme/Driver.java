import java.util.*;
import java.io.*;


public class Driver {
    public static void main(String[] args) {
        // new Driver();
        new Driver("sample.txt");
    }

    public Driver() {
        // PrefixEvaluator pEvaluator = new PrefixEvaluator("( block ( ( q ( % ( block ( )  ( block ( ( a ( block ( ( a c ) ( f 4 ) ( h ( / ( + 9 9 ) 8 ) ) ( z j ) )  ( - h ( % ( ^ 3 ( + 6 10 ) ) ( block ( ( q 1 ) ( b 6 ) ( t 10 ) ( n 8 ) )  a )  ) ) )  ) ( f e ) ( l 5 ) ( o 11 ) )  11 )  )  ( * 2 10 ) ) ) ( a 10 ) ( y ( block ( ( p 1 ) ( t 6 ) ( g 7 ) ( w 11 ) ( o 4 ) )  ( block ( ( r ( block ( ( e 11 ) ( v 5 ) ( z 3 ) )  3 )  ) ( s w ) ( u ( - ( + ( - ( block ( )  w )  ( % ( block ( ( a 2 ) ( u 2 ) ( j 5 ) ( l 11 ) ( n 10 ) )  ( * 7 9 ) )  w ) ) 2 ) 10 ) ) ( f ( + ( block ( )  9 )  t ) ) ( h ( block ( )  ( block ( ( a 10 ) ( l 10 ) )  11 )  )  ) ( m t ) )  ( ^ t 10 ) )  )  ) )  y )");
        PrefixEvaluator pEvaluator = new PrefixEvaluator("( block ( ( q 5 ) ( f 7 ) )  9 ) ");


        // System.out.println(pEvaluator.evaluateExpressions());
    }

    public Driver(String file) {

        try {
            FileReader fReader = new FileReader(new File(file));
            Scanner scanner = new Scanner(fReader);
            
            while(scanner.hasNext()) {
                String input = scanner.nextLine();
                PrefixEvaluator pEvaluator = new PrefixEvaluator(input);
                // System.out.println(pEvaluator.toInfix());
                // System.out.println(pEvaluator.evaluateExpressions());
            }

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

    private String evaluate(String expression) {
        Scanner scanner = new Scanner(expression);
        String firstChar = scanner.next();
        if(Character.isDigit(firstChar.charAt(0)))
        {
            scanner.close();
            return firstChar;
        }
        if(firstChar.equals("("))
        {
            if(scanner.hasNext("("))
            {

            }
            // scanner.useDelimiter("\\)");
        }
        String operator = scanner.next();
        int exp1 = Integer.parseInt(evaluate(scanner.next()));
        int exp2 = Integer.parseInt(evaluate(scanner.next()));
        scanner.close();
        
        if( operator.equals("+")) 
        {
            return exp1 + exp2 + "";
        } 
        else if (operator.equals("-")) 
        {
            return exp1 - exp2 + "";
        } 
        else if (operator.equals("*")) 
        {
            return exp1 * exp2 + "";
        } 
        else if (operator.equals("/")) 
        {
            return exp1 / exp2 + "";
        } 
        else if (operator.equals("^")) 
        {
            return (int) Math.pow(exp1, exp2) + "";
        } 
        else if (operator.equals("%")) 
        {
            return exp1 % exp2 + "";
        }
        else 
        {
            return "undefined";
        }
    }


}

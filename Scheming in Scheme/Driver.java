import java.util.*;
import java.io.*;


public class Driver {
    public static void main(String[] args) {
        new Driver("sample.txt");
    }

    public Driver() {
    }

    public Driver(String file) {

        try {
            FileReader fReader = new FileReader(new File(file));
            Scanner scanner = new Scanner(fReader);
            
            while(scanner.hasNext()) {
                String input = scanner.nextLine();
                PrefixEvaluator pEvaluator = new PrefixEvaluator(input);
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
}

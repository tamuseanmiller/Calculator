import java.io.FileNotFoundException;
import java.io.IOException;

public class Driver {

//  Main method, just to test the Calculate class
    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        Calculate calc = new Calculate(fileName);
        calc.readFile();
        calc.writeResults();
    }
}

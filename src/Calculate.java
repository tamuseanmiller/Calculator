import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import static java.lang.Math.pow;

public class Calculate {

    //  Private Instance Variables for the 3 stacks, the priority table, as well as the input file and result vector
    private Stack<Character> op;
    private Stack<Integer> priority;
    private Stack<Double> value;
    private HashMap<Character, Integer> priorityTable;
    private ArrayList<Double> results;
    private Scanner file;

    //  Default Constructor
    public Calculate() {
        op = new Stack<>();
        priority = new Stack<>();
        value = new Stack<>();
        priorityTable = new HashMap<>();
        results = new ArrayList<>();
        priority.push(-1);
        op.push('$');
        priorityTable.put('+', 1);
        priorityTable.put('-', 1);
        priorityTable.put('*', 2);
        priorityTable.put('/', 2);
        priorityTable.put('%', 2);
        priorityTable.put('^', 5);
        priorityTable.put('(', 6);
        priorityTable.put(')', -2);
        priorityTable.put('$', -1);
        priorityTable.put('e', -2);
    }

    //  Initialized Constructor
    public Calculate(String filename) throws FileNotFoundException {
        op = new Stack<>();
        priority = new Stack<>();
        value = new Stack<>();
        priorityTable = new HashMap<>();
        results = new ArrayList<>();
        priority.push(-1);
        op.push('$');
        file = new Scanner(new File(filename));
        priorityTable.put('+', 1);
        priorityTable.put('-', 1);
        priorityTable.put('*', 2);
        priorityTable.put('/', 2);
        priorityTable.put('%', 2);
        priorityTable.put('^', 5);
        priorityTable.put('(', 6);
        priorityTable.put(')', -2);
        priorityTable.put('$', -1);
        priorityTable.put('e', -2);
    }

    //  Checks to see if char passed in is in the priority table
    private boolean isOp(char val) {
        return priorityTable.containsKey(val);
    }

    // Validates file
    void readFile() {
        push();
    }

    // Writes results to a file named results
    public void writeResults() throws IOException {
        FileWriter result = new FileWriter("results.txt");
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i) == Double.MAX_VALUE) {
                result.write("invalid\n");
            } else {
                result.write(String.valueOf(results.get(i)) + '\n');
            }
        }
        result.close();
    }

    //  Method to traverse through each line of file and is the main source of calculation
    public void push() {
        String line;
        char in;

//  Traverses every line of file
        while (file.hasNextLine()) {
            line = file.nextLine();

            String[] words = line.split(" ");
//      Traverses every word in line
            for (String word : words) {
                in = word.charAt(0);

//          Checks for an invalid input that is just an empty word
                if (word.isEmpty()) {
                    results.add(Double.MAX_VALUE);

//          Main, checks for an operation
                } else if (isOp(in)) {
                    if (in == '(') {
                        op.push(in);
                        priority.push(0);
                    } else if (in == ')') {
                        while (op.peek() != '(') {
                            if (op.peek() == '$') {
                                results.add(Double.MAX_VALUE);
                                break;
                            } else if (op.peek() == '(') {
                                op.pop();
                                priority.pop();
                                break;
                            }
                            popAndProcess();
                        }
                        op.pop();
                        priority.pop();

//              Checks for ')'
                    } else if (priorityTable.get(in) == -2) {
                        op.push(in);

//              Checks for if it is in the Order of Operations
                    } else if (priorityTable.get(in) >= priority.peek()) {
                        op.push(in);
                        if (in == '^') {
                            priority.push(4);
                        } else {
                            priority.push(priorityTable.get(in));
                        }

//              Anything else
                    } else {

//                  Checks for misleading parentheses
                        while (op.peek() != '$') {
                            if (op.peek() == '(') {
                                break;
                            }
                            popAndProcess();
                        }
                        op.push(in);
                        priority.push(priorityTable.get(in));
                    }

//          Else is just any immediate value
                } else {
                    value.push(Double.valueOf(word));
                }
            }


//        After line end pushes back result after one final popAndProcess()
            while (value.size() != 1 && !value.empty()) {
                popAndProcess();
            }

//        Checks for an invalid equation that is missing a parentheses
            if (op.peek() != '$' && !value.empty()) {
                results.add(Double.MAX_VALUE);
                value.pop();
                op.pop();
                priority.pop();

//      Checks for something like ()
            } else if (value.empty()) {
                results.add(Double.MAX_VALUE);
            } else if (!value.empty()) {
                results.add(value.peek());
                value.pop();
            }
        }

    }

    //  This method does the specific operation on top of the stack
    public void popAndProcess() {
        //  Check to make sure there is an operation possible
        if (op.peek() == '$') {
            return;
        }
        double temp = value.peek();
        value.pop();

//  Determine which operator is passed and complete that operation
        switch (op.peek()) {
            case '+':
                temp = value.peek() + temp;
                break;
            case '-':
                temp = value.peek() - temp;
                break;
            case '*':
                temp = value.peek() * temp;
                break;
            case '%':
                temp = value.peek() % temp;
                break;
            case '/':
                temp = value.peek() / temp;
                break;
            case '^':
                temp = pow(value.peek(), temp);
        }
        value.pop();
        value.push(temp);
        priority.pop();
        op.pop();
    }

}
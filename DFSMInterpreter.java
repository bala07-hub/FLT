// Name: Balamanikanta Anantha
// CWID: A20402153
// Assignment Start Date: 08-27-2024
// Assignment Submitted Date:09-02-2024
// Assignment Due Date: 09-03-2024
// Course Name: FLT
// Programming Assignment-1

import java.io.*;
import java.util.*;

public class DFSMInterpreter 
{

    // converts an array of strings into a single string
    private static String arrayToString(String[] array) 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) 
        {
            sb.append(array[i]);
            if (i < array.length - 1) 
            {
                sb.append(" "); 
            }
        }
        return sb.toString();
    }

    // To view transistion table
    private static String deepMapToString(int[][] array, String[] alphabet) 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append("{");
            for (int j = 0; j < array[i].length; j++) 
            {
                sb.append(alphabet[j] + "=" + array[i][j]);
                if (j < array[i].length - 1) 
                {
                    sb.append(", ");
                }
            }
            sb.append("}");
            if (i < array.length - 1) 
            {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    // Appending each integer to string builder
    private static String intArrayToString(int[] array) 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) 
        {
            sb.append(array[i]);
            if (i < array.length - 1) 
            {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    // For Debugging
    private static String charArrayToString(char[] array) 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) 
        {
            sb.append(array[i]);
            if (i < array.length - 1) 
            {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    // Function to read the alphabet from the machine.txt
    // Function to read the alphabet from the machine specification file
    private static String[] readAlphabet(String line) throws IOException {
        if (line == null || line.trim().isEmpty()) {
            throw new IOException("Alphabet section is empty.");
        }
        String[] parts = line.trim().split("\\s+");
        if (parts.length < 1 || !line.matches("[a-zA-Z\\s]+")) {
            throw new IOException("Invalid format in alphabet definition.");
        }
        return parts;
    }
    // Function to read the transition table from the machine.txt
private static int[][] readTransitionTable(List<String> lines, String[] alphabet) throws IOException {
    if (lines.size() < 3) { // Not enough lines for a valid table
        throw new IOException("Insufficient lines for a valid transition table.");
    }

    // Determine the start and end lines for the transition table, skipping empty lines
    int startLine = 1; // Start after the alphabet line
    while (startLine < lines.size() - 1 && lines.get(startLine).trim().isEmpty()) {
        startLine++; // Skip empty lines
    }
    int endLine = lines.size() - 2; // Stop before the accepting states line
    while (endLine > startLine && lines.get(endLine).trim().isEmpty()) {
        endLine--; // Skip empty lines
    }

    int[][] transitionTable = new int[endLine - startLine + 1][alphabet.length];

    for (int i = startLine; i <= endLine; i++) {
        String line = lines.get(i).trim(); // Trim spaces from the line

        if (line.isEmpty()) {
            continue; // Skip empty lines within the transition table
        }

        if (!line.matches("(\\d+\\s*)+")) { // Ensure line contains only numbers
            throw new IOException("Transition table line format error at line " + (i + 1));
        }

        String[] states = line.split("\\s+");
        if (states.length != alphabet.length) {
            throw new IOException("Transition states count mismatch with alphabet size at line " + (i + 1));
        }

        for (int j = 0; j < states.length; j++) {
            try {
                int state = Integer.parseInt(states[j]);
                if (state < 1 || state > (endLine - startLine + 1)) { // Check if the state is out of the defined range
                    throw new IOException("Invalid DFSM: State " + state + " is not defined in the transition table.");
                }
                transitionTable[i - startLine][j] = state;
            } catch (NumberFormatException e) {
                throw new IOException("Invalid number format in transition table at line " + (i + 1) + ", column " + (j + 1));
            }
        }
    }
    return transitionTable;
}

    // Function to read the accepting states from the machine.txt
    private static int[] readAcceptingStates(List<String> lines, int startLine) throws IOException {
        // Skip empty lines to find the accepting states line
        while (startLine < lines.size() && lines.get(startLine).trim().isEmpty()) {
            startLine++;
        }

        if (startLine >= lines.size()) {
            throw new IOException("Accepting states line is missing.");
        }

        String line = lines.get(startLine).trim();
        if (line.isEmpty()) {
            throw new IOException("Accepting states line is empty.");
        }

        String[] parts = line.split("\\s+");
        int[] states = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                states[i] = Integer.parseInt(parts[i]);
                if (states[i] <= 0) {
                    throw new IOException("Invalid state number in accepting states: " + parts[i]);
                }
            } catch (NumberFormatException e) {
                throw new IOException("Invalid number format in accepting states: " + parts[i]);
            }
        }
        return states;
    }
    
    //Constructs a character array using input.txt

    private static char[] readInputString(BufferedReader br) throws IOException 
    {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) 
        {
            for (char ch : line.toCharArray()) 
            {
                if (ch != ' ') { // Ignore spaces
                    sb.append(ch);
                }
            }
        }
        if (sb.length() == 0) 
        {
            throw new IOException("Input string is empty.");
        }
        return sb.toString().toCharArray();
    }
    

    //To simulate the DFMS based on machine.txt and input.txt

    private static String simulateDFSM(int[][] transitionTable, int[] acceptingStates, char[] inputString, String[] alphabet) 
    {
        if (inputString.length == 0) 
        {
            return "Error: Input string is empty.";
        }

        int currentState = 1; // Heree, assuming the starting state is 1
        for (char symbol : inputString) 
        {
            int symbolIndex = -1;
            for (int i = 0; i < alphabet.length; i++) 
            {
                if (alphabet[i].equals(String.valueOf(symbol))) 
                {
                    symbolIndex = i;
                    break;
                }
            }

            if (symbolIndex == -1) 
            {
                return "Error: The symbol '" + symbol + "' is not recognized in the DFA alphabet.";
            }

            currentState = transitionTable[currentState - 1][symbolIndex];

            if (currentState < 1 || currentState > transitionTable.length) 
            {
                return "Error: Reached an undefined state " + currentState + ".";
            }
        }

        for (int state : acceptingStates) 
        {
            if (state == currentState) 
            {
                return "Accepted";
            }
        }

        return "Rejected";
    }

    public static void main(String[] args) 
    {

        //To check two arguments passed to read machine.txt & input.txt
        if (args.length != 2) 
        {
            System.out.println("Usage: java DFSMInterpreter <machine_file> <input_file>");
            return;
        }
    
        String machineFile = args[0];
        String inputFile = args[1];
    
        try 
        {
            List<String> lines = new ArrayList<>();
            try (BufferedReader machineReader = new BufferedReader(new FileReader(machineFile))) 
            {
                String line;
                while ((line = machineReader.readLine()) != null) 
                {
                    lines.add(line);
                }
            }
    
            if (lines.isEmpty()) 
            {
                throw new IOException("Machine file is empty.");
            }
    
            String[] alphabet = readAlphabet(lines.get(0));
            int[][] transitionTable = readTransitionTable(lines, alphabet);
          //  int numberOfStates = transitionTable.length; 
            int[] acceptingStates = readAcceptingStates(lines, lines.size() - 1);

            char[] inputString;
            try (BufferedReader inputReader = new BufferedReader(new FileReader(inputFile))) 
            {
                inputString = readInputString(inputReader);
            }

            // System.out.println("Alphabet: " + arrayToString(alphabet));
            // System.out.println("Transition Table: " + deepMapToString(transitionTable, alphabet));
            // System.out.println("Accepting States: " + intArrayToString(acceptingStates));
            // System.out.println("Input String: " + charArrayToString(inputString));
    
            String result = simulateDFSM(transitionTable, acceptingStates, inputString, alphabet);
            // Prints DFSM is accepted or rejected for given string
            System.out.println(result);
    
        }
         catch (IOException e) 
         {
            System.err.println("Error: " + e.getMessage());
         }
    }
}


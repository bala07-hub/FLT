// Name: Balamanikanta Anantha
// CWID: A20402153
// Assignment Start Date: 09-03-2024
// Assignment Submitted Date:09-10-2024
// Assignment Due Date: 09-10-2024
// Course Name: FLT
// Programming Assignment-2
import java.io.*;

public class NDFSMBuilder {

    
    static class NDFSM {
        private int initialState;
        private int[] acceptingStates;
        private char[] alphabet;
        private int[][][] transitions; // Transitions for each state and input symbol

        public NDFSM(char[] alphabet, int maxStates) 
        {   
            //Initial State "1"
            this.initialState = 1;  
            //Last state will always be the final state
            this.acceptingStates = new int[maxStates + 1]; 
            this.alphabet = alphabet;
            // to handle transistions
            this.transitions = new int[maxStates + 1][alphabet.length][maxStates + 1]; 

            // Initialize transitions to -1 indicating no transitions
            for (int i = 1; i <= maxStates; i++) 
            {
                for (int j = 0; j < alphabet.length; j++) 
                {
                    for (int k = 1; k <= maxStates; k++) 
                    {
                        transitions[i][j][k] = -1;
                    }
                }
            }

            // Initialize accepting states to -1 indicating when theree are no accepting state set
            for (int i = 1; i <= maxStates; i++) 
            {
                acceptingStates[i] = -1;
            }
        }

        // Adds to given state and new states
        public void addTransition(int fromState, char symbol, int toState) 
        {
            int symbolIndex = getSymbolIndex(symbol);
            if (symbolIndex == -1) 
            {
                return; // Invalid symbols
            }
            // TO find the first empty spot in the transitions array
            for (int i = 1; i < transitions[fromState][symbolIndex].length; i++) 
            {
                if (transitions[fromState][symbolIndex][i] == -1) 
                {
                    transitions[fromState][symbolIndex][i] = toState;
                    break;
                }
            }
        }

        // Adds a self-loop with all symbols for initial and final states
        public void addSelfLoop(int state)
        {
            for (int i = 0; i < alphabet.length; i++) {
                addTransition(state, alphabet[i], state);
            }
        }

        // To Set an accepting state (always will be the final state)
        public void setAcceptingState(int state) 
        {
            for (int i = 1; i < acceptingStates.length; i++) 
            {
                if (acceptingStates[i] == -1) 
                {
                    acceptingStates[i] = state;
                    break;
                }
            }
        }

        // To get symbol index
        private int getSymbolIndex(char symbol) 
        {
            for (int i = 0; i < alphabet.length; i++) {
                if (alphabet[i] == symbol) {
                    return i;
                }
            }
            return -1; // Symbol not found
        }

        // To generate NDFSM specfication file 
        public void writeToFile(String filename) throws IOException 
        {
            FileWriter writer = new FileWriter(filename);

            // First writes alphabets in given order
            for (int i = 0; i < alphabet.length; i++) 
            {
                writer.write(alphabet[i] + (i < alphabet.length - 1 ? " " : ""));
            }
            writer.write("\n\n"); // TO add space after alphabets

            // Write transitions
            for (int state = 1; state < transitions.length; state++) 
            {  // Start State will be "1" always
                StringBuilder lineBuilder = new StringBuilder();
                boolean hasValidTransitions = false; //to check current state has any valid transistions
                for (int symbolIndex = 0; symbolIndex < alphabet.length; symbolIndex++) 
                {
                    StringBuilder transitionPart = new StringBuilder("[");
                    boolean first = true;

                    for (int i = 1; i < transitions[state][symbolIndex].length; i++) 
                    {
                        if (transitions[state][symbolIndex][i] != -1) 
                        {
                            if (!first)
                            {
                                transitionPart.append(", ");
                            }
                            transitionPart.append(transitions[state][symbolIndex][i]);
                            first = false;
                            hasValidTransitions = true; //flags valid transitions
                        }
                    }
                    transitionPart.append("] ");
                    lineBuilder.append(transitionPart);
                }

                // writes when we have atleast one valid transistion in the specifition 
                if (hasValidTransitions) 
                {
                    writer.write(lineBuilder.toString().trim() + "\n");
                }
            }

            writer.write("\n"); // Extra Space after transistions

            // TO write accepting state
            boolean hasAcceptingStates = false; // TO check do we have any accepting states
            for (int i = 1; i < acceptingStates.length; i++) 
            {
                if (acceptingStates[i] != -1) 
                {
                    writer.write((hasAcceptingStates ? " " : "") + acceptingStates[i]);
                    hasAcceptingStates = true;
                }
            }

            if (hasAcceptingStates) 
            { // writes if we have any accepting states
                writer.write("\n");
            }

            writer.close();
        }
    }

    // To generate NDFSM from given pattern.txt
    public static void buildNDFSMFromString(String outputFile, String pattern) 
    {
        if (!pattern.matches("[a-zA-Z]+")) 
        {
            System.err.println("Error: The pattern must contain only lowercase or uppercase alphabets."); //allows only alphabets
            return;
        }

        char[] alphabet = extractAlphabet(pattern);
        int maxStates = pattern.length() + 2; 
        NDFSM ndfsm = new NDFSM(alphabet, maxStates);

        int currentState = 1; // Always starts from initial state "1"

        // Selfloop with all alphabets for initial state
        ndfsm.addSelfLoop(currentState); 

        // To Add transition from given pattern
        ndfsm.addTransition(currentState, pattern.charAt(0), currentState + 1);

        // Building NDFSM transitions for remaining pattern given
        for (int i = 1; i < pattern.length(); i++)
        {
            char c = pattern.charAt(i);
            currentState++;
            ndfsm.addTransition(currentState, c, currentState + 1);
        }

        // selfloop for final state with all symbols
        ndfsm.addSelfLoop(currentState + 1); 

        // Last state will always be the accepting state in this 
        ndfsm.setAcceptingState(currentState + 1);

        try
        {
            ndfsm.writeToFile(outputFile);
            System.out.println("NDFSM written to " + outputFile);
        } catch (IOException e) 
        {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    //Extracts alphabet will ASCII range
    private static char[] extractAlphabet(String pattern) {
        boolean[] present = new boolean[128]; // ASCII range
        int uniqueCount = 0;

        for (int i = 0; i < pattern.length(); i++) 
        {
            char c = pattern.charAt(i); 
            if (!present[c]) {
                present[c] = true;
                uniqueCount++;
            }
        }

        char[] alphabet = new char[uniqueCount];
        int index = 0;
        for (int i = 0; i < 128; i++) 
        {
            if (present[i]) {
                alphabet[index++] = (char) i;
            }
        }

        return alphabet;
    }

    // To read given patter from the file (pattern.txt)
    private static String readPatternFromFile(String filename) throws IOException 
    {
        StringBuilder patternBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
        {
            String line;
            while ((line = br.readLine()) != null) 
            {
                patternBuilder.append(line.trim());
            }
        }
        return patternBuilder.toString();
    }

    public static void main(String[] args) 
    {
        //must pass two arguments one file to store NDFSM specification and one more for pattern.txt
        if (args.length != 2) 
        {
            System.out.println("Usage: java NDFSMBuilder <output file> <pattern file>");
            return;
        }

        String outputFile = args[0];
        String patternFile = args[1];

        try 
        {
            String pattern = readPatternFromFile(patternFile);
            buildNDFSMFromString(outputFile, pattern);
        } catch (IOException e) 
        {
            System.err.println("Error reading pattern file: " + e.getMessage());
        }
    }
}


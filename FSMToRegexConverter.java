//Name: Balamanikanta Anantha
//Student id: A20402153
//Assignment Start date: 09/17/2024
//Assignment Due data:09/24/2024
//Assignment Submitted date: 09/23/2024



import java.io.*;
import java.util.*;

public class FSMToRegexConverter 
{

    public static void main(String[] args) throws Exception 
    {
        // Check if  one argument is passed or not
        if (args.length != 1) 
        {
            System.out.println("ERROR: Please provide the correct number of arguments.");
            return;
        }

        String inputFileName = args[0];
        File file = new File(inputFileName);
        if (!file.exists()) 
        {
            System.out.println("ERROR: File not found.");
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));

        // Reading and parsing the alphabet/symbols
        List<String> alphabets = new ArrayList<>();
        String readLine;
        while ((readLine = reader.readLine()) != null) 
        {
            if (readLine.trim().isEmpty()) break;
            for (char ch : readLine.toCharArray()) 
            {
                if (ch != ' ') alphabets.add(String.valueOf(ch));
            }
        }

        int lengthOfAlpha = alphabets.size();
        List<HashMap<String, Integer>> transitionTable = new ArrayList<>();
        int maxState = 0;

        // Reading and parsing the transition matrix
        while ((readLine = reader.readLine()) != null) 
        {
            if (readLine.trim().isEmpty()) break;
            List<String> currentRow = Arrays.asList(readLine.trim().split("\\s+"));
            if (currentRow.size() != lengthOfAlpha) 
            {
                System.out.println("ERROR: Mismatch in number of alphabets and transitions.");
                return;
            }

            HashMap<String, Integer> rowMap = new HashMap<>();
            for (int i = 0; i < lengthOfAlpha; i++) {
                int nextState = Integer.parseInt(currentRow.get(i));
                if (nextState < 1) 
                {
                    System.out.println("ERROR: Invalid state value.");
                    return;
                }
                maxState = Math.max(maxState, nextState);
                rowMap.put(alphabets.get(i), nextState);
            }
            transitionTable.add(rowMap);
        }

        // Reading and parsing the final states
        List<Integer> finalStates = new ArrayList<>();
        while ((readLine = reader.readLine()) != null) 
        {
            List<String> finalStatesInput = Arrays.asList(readLine.trim().split("\\s+"));
            for (String state : finalStatesInput) {
                int finalState = Integer.parseInt(state);
                if (finalState > maxState) {
                    System.out.println("ERROR: Final state exceeds the highest state value.");
                    return;
                }
                finalStates.add(finalState);
            }
        }  

        int numberOfStates = transitionTable.size();
        //when there are no accepting states regex will be phi
        if (finalStates.isEmpty()) 
        {
            System.out.println("Regular Expression: phi (No accepting states)");
            return;
        }

        // Construct the adjacency matrix
        String[][] adjMatrix = new String[numberOfStates + 2][numberOfStates + 2];
        for (int i = 0; i < numberOfStates + 2; i++) 
        {
            Arrays.fill(adjMatrix[i], "phi");
        }

        // Add epsilon transitions at the start
        adjMatrix[0][1] = "eps";
        for (int state : finalStates) 
        {
            adjMatrix[state][numberOfStates + 1] = "eps";
        }

        // Fill the transition table in the adjacency matrix
        for (int i = 0; i < transitionTable.size(); i++) 
        {
            HashMap<String, Integer> currentRow = transitionTable.get(i);
            int currentState = i + 1;
            for (Map.Entry<String, Integer> entry : currentRow.entrySet()) 
            {
                String alphabet = entry.getKey();
                int nextState = entry.getValue();
                if (adjMatrix[currentState][nextState].equals("phi")) 
                {
                    adjMatrix[currentState][nextState] = alphabet;
                } else 
                {
                    adjMatrix[currentState][nextState] += " + " + alphabet;
                }
            }
        }

        // Remove states iteratively to construct the final regex
        for (int stateToRemove = numberOfStates; stateToRemove > 0; stateToRemove--) 
        {
            List<Integer> incomingStates = getIncomingStates(adjMatrix, stateToRemove);
            List<Integer> outgoingStates = getOutgoingStates(adjMatrix, stateToRemove);

            for (int incoming : incomingStates) 
            {
                for (int outgoing : outgoingStates) 
                {
                    String regexBetweenStates = constructRegex(adjMatrix, incoming, stateToRemove, outgoing);
                    if (adjMatrix[incoming][outgoing].equals("phi")) 
                    {
                        adjMatrix[incoming][outgoing] = regexBetweenStates;
                    } else 
                    {
                        adjMatrix[incoming][outgoing] += " + " + regexBetweenStates;
                    }
                }
            }

            // Remove transitions from and to the state
            for (int i = 0; i < numberOfStates + 2; i++) 
            {
                adjMatrix[stateToRemove][i] = "phi";
                adjMatrix[i][stateToRemove] = "phi";
            }
        }

        // Output the final regular expression
        System.out.println("Regular Expression: " + adjMatrix[0][numberOfStates + 1]);
    }

    // Get outgoing states from the given state
    public static List<Integer> getOutgoingStates(String[][] matrix, int state) 
    {
        List<Integer> outgoingStates = new ArrayList<>();
        for (int i = 0; i < matrix[state].length; i++) 
        {
            if (!matrix[state][i].equals("phi") && i != state) 
            {
                outgoingStates.add(i);
            }
        }
        return outgoingStates;
    }

    // Get incoming states to the given state
    public static List<Integer> getIncomingStates(String[][] matrix, int state) 
    {
        List<Integer> incomingStates = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) 
        {
            if (!matrix[i][state].equals("phi") && i != state) 
            {
                incomingStates.add(i);
            }
        }
        return incomingStates;
    }

    // Construct regex between incoming and outgoing states via the stateToRemove
    public static String constructRegex(String[][] matrix, int incoming, int stateToRemove, int outgoing) 
    {
        String regex;
        if (matrix[stateToRemove][stateToRemove].equals("phi")) 
        {
            regex = matrix[incoming][stateToRemove] + "." + matrix[stateToRemove][outgoing];
        } else 
        {
            regex = matrix[incoming][stateToRemove] + ".(" + matrix[stateToRemove][stateToRemove] + ")*." + matrix[stateToRemove][outgoing];
        }
        return regex;
    }
}

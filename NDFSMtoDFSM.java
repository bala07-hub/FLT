// Name: Balamanikanta Anantha
// CWID: A20402153
// Assignment Start Date: 09-03-2024
// Assignment Submitted Date:09-10-2024
// Assignment Due Date: 09-10-2024
// Course Name: FLT
// Programming Assignment-2

import java.io.*;
import java.util.*;

public class NDFSMtoDFSM {

    //To convert NDFSM to DFSM
    public static void convertToDFSM(String inputFileName, String outputFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {

            //Read symbols 
            String alphabetLine = reader.readLine();
            if (alphabetLine == null || alphabetLine.trim().isEmpty()) {
                throw new IOException("Alphabet line is missing or empty.");
            }
            String[] alphabet = customSplit(alphabetLine.trim(), ' ');
            System.out.println("Parsed Alphabet: " + arrayToString(alphabet)); // For Debugging

            // Read transitions
            List<String[]> transitions = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) { 
                line = line.trim();
                if (line.isEmpty()) continue; // Skip empty lines  

                // Process each transition line
                if (line.startsWith("[") && line.endsWith("]")) {
                    String[] parsedTransitions = new String[alphabet.length];
                    String[] parts = line.split("\\]\\s*\\[");
                    for (int i = 0; i < parts.length && i < alphabet.length; i++) {
                        String part = parts[i].replace("[", "").replace("]", "").trim(); 
                        parsedTransitions[i] = part.isEmpty() ? "[]" : "[" + part + "]";
                    }
                    transitions.add(parsedTransitions);
                } else {
                    break; // End of transitions
                }
            }

            int maxState = transitions.size();  // The maximum state index allowed
            validateTransitions(transitions, maxState); // Validate transitions for state bounds

            // Check if the first state in the transitions is 1
            if (!transitions.isEmpty()) {
                boolean validInitialState = false;
                for (String transition : transitions.get(0)) {
                    if (transition.contains("1")) {
                        validInitialState = true;
                        break;
                    }
                }
                if (!validInitialState) {
                    throw new IOException("Invalid DFSM: Initial state does not start with state 1.");
                }
            }

            // To Print parsed transitions for debugging
            System.out.println("Parsed Transitions:");
            for (String[] transition : transitions) {
                System.out.println(arrayToString(transition));
            }

            // To read accepting states
            String acceptingStatesLine = line;
            if (acceptingStatesLine == null || acceptingStatesLine.trim().isEmpty()) {
                throw new IOException("Accepting states line is missing or empty.");
            }
            String[] acceptingStates = customSplit(acceptingStatesLine.trim(), ' ');
            System.out.println("Parsed Accepting States: " + arrayToString(acceptingStates)); 

            // NDFSM to DFSM conversion logic
            List<String[]> dfsmTransitions = new ArrayList<>();
            Map<String, Integer> stateMapping = new HashMap<>();
            List<Set<Integer>> newStates = new ArrayList<>();

            // Start with the initial state of NDFSM (set containing only the initial state 1)
            Set<Integer> initialState = new HashSet<>();
            initialState.add(1);  // Start state is 1 now
            newStates.add(initialState);
            stateMapping.put(setToString(initialState), 1); // mapping new state to DFSM

            // Process all new states
            int dfsmStateCounter = 2; // Start DFSM state from "2"
            for (int i = 0; i < newStates.size(); i++) {
                Set<Integer> currentState = newStates.get(i);
                String[] currentTransitions = new String[alphabet.length];

                // transitions for each symbol 
                for (int j = 0; j < alphabet.length; j++) {
                    String symbol = alphabet[j];
                    Set<Integer> nextState = new HashSet<>();

                    for (Integer state : currentState) {
                        String transition = transitions.get(state - 1)[j]; 
                        if (!transition.equals("[]")) {
                            String[] reachableStates = customSplit(transition.replace("[", "").replace("]", "").trim(), ',');
                            for (String s : reachableStates) {
                                nextState.add(Integer.parseInt(s.trim()));
                            }
                        }
                    }

                    // If the next state is not already in our list of new states, add It
                    String nextStateStr = setToString(nextState);
                    if (!stateMapping.containsKey(nextStateStr)) {
                        stateMapping.put(nextStateStr, dfsmStateCounter++);
                        newStates.add(nextState);
                    }

                    // Add the DFSM state corresponding to this next state to the transition table
                    currentTransitions[j] = String.valueOf(stateMapping.get(nextStateStr));
                }

                // Add computed transitions for the current DFSM state
                dfsmTransitions.add(currentTransitions);
            }

            // To FIND DFSM accepting states
            Set<Integer> dfsmAcceptingStates = new HashSet<>();
            for (Set<Integer> stateSet : newStates) {
                for (String accState : acceptingStates) {
                    if (stateSet.contains(Integer.parseInt(accState))) {
                        dfsmAcceptingStates.add(stateMapping.get(setToString(stateSet)));
                        break;
                    }
                }
            }

            // To generate DFSM specification file
            writer.write(String.join(" ", alphabet) + "\n\n"); //write alphabet then add extra space

            for (String[] transition : dfsmTransitions) {
                for (int j = 0; j < transition.length; j++) {
                    // Write transitions without brackets to match with machine.txt from 1(a)
                    writer.write(transition[j].replaceAll("[\\[\\]]", "") + (j < transition.length - 1 ? " " : ""));
                }
                writer.write("\n");
            }
            writer.write("\n");
            for (Integer acceptingState : dfsmAcceptingStates) {
                writer.write(acceptingState + " ");
            }
            //add extra space after transistions
            writer.write("\n");

            System.out.println("DFSM generated and written to " + outputFileName);

        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }

    // Method to validate transitions for state bounds
    private static void validateTransitions(List<String[]> transitions, int maxState) throws IOException {
        for (int i = 0; i < transitions.size(); i++) {
            for (String transition : transitions.get(i)) {
                if (!transition.equals("[]")) {
                    String[] reachableStates = customSplit(transition.replace("[", "").replace("]", "").trim(), ',');
                    for (String stateStr : reachableStates) {
                        int state = Integer.parseInt(stateStr.trim());
                        if (state < 1 || state > maxState) {
                            throw new IOException("Invalid state number: " + state + ". State exceeds the maximum allowed states (" + maxState + ").");
                        }
                    }
                }
            }
        }
    }

    // Helper methods

    private static String[] customSplit(String str, char delimiter) {
        List<String> splitResult = new ArrayList<>(); // Using List for dynamic size
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == delimiter) {
                if (current.length() > 0) {
                    splitResult.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            splitResult.add(current.toString());
        }

        return splitResult.toArray(new String[0]); // Convert List to Array
    }

    private static String arrayToString(String[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static String setToString(Set<Integer> set) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : set) {
            sb.append(i).append(",");
        }
        return sb.toString();
    }

    // Main
    public static void main(String[] args) {
        if (args.length != 2) 
        {
            System.out.println("Usage: java NDFSMtoDFSM <input_file> <output_file>");
            return;
        }

        String inputFileName = args[0];
        String outputFileName = args[1];

        convertToDFSM(inputFileName, outputFileName);
    }
}

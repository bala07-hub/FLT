// Name: Balamanikanta Anantha
// CWID: A20402153
// Assignment Start Date: 09-03-2024
// Assignment Submitted Date:09-10-2024
// Assignment Due Date: 09-10-2024
// Course Name: FLT
// Programming Assignment-2
// Changes by dev1 from copy1 branch
 README for NDFSMtoDFSM.java

Overview

The NDFSMtoDFSM program is desgined to convert a Non-Deterministic Finite State Machine (NDFSM) specification into a Deterministic Finite State Machine (DFSM) specification. The program reads an NDFSM from an input file, processes the transitions, and generates the equivalent DFSM. The resulting DFSM specification is saved to an output file.



NDFSM to DFSM Conversion: Converts an NDFSM, described in a text file, into its equivalent DFSM.
Output Specification: Generates an output file containing the DFSM specification, including states, transitions, and accepting states.


NDFSMtoDFSM.java: The Java source file containing the implementation for converting NDFSM to DFSM.
ndfsm.txt: A text file containing the NDFSM specification (input file).
dfsm.txt: The output file where the DFSM specification will be saved.

How to run

   javac NDFSMtoDFSM.java

Prepare the Input File:
   Create a text file named `ndfsm.txt` in the same directory as `NDFSMtoDFSM.java`. This file should contain the NDFSM specification.

   Example:

  ndfsm.txt:
   a b

   [1, 2] [1]
   [] [3]
   [4] []
   [] [5]
   [5] [5]

   5
   


   java NDFSMtoDFSM ndfsm.txt dfsm.txt

  ndfsm.txt: This is the input file containing the NDFSM specification.
  dfsm.txt: This is the file where the generated DFSM specification will be saved.

Check the Output:

   After running the program, check the `dfsm.txt` file to see the generated DFSM specification.

 Output Format

The output file (`dfsm.txt`) will have the following format:

1. Alphabet Line: Lists all unique characters (symbols) in the NDFSM, separated by spaces.
2. Transitions: Lists state transitions for each symbol without brackets, following the format used in `machine.txt` from Assignment 1a.
3. Accepting States: Lists all the accepting states at the end of the file.

Example Output (`dfsm.txt`):
a b

2 1
2 3
4 1
4 5
4 6
4 6

4 5 6 

- The initial state is always assumed to be `1`.
- The conversion logic maps each NDFSM state combination to a unique DFSM state.
- Ensure both `ndfsm.txt` and `dfsm.txt` are in the same directory as the Java program when running it.
- The program reads the input file dynamically and processes transitions for each state and symbol.



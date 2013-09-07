/*
 * Author: Zachary McHenry, zmchenry2011@my.fit.edu
 * Course: CSE 4051, Section 01, Fall 2013
 * Project: proj02 , Propositional Checker
 */

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Checker {
    private static final int NUM_OF_BITS = 64;
    
    // BitSet does not include an implies method.
    public BitSet implication (final BitSet poppedX,
            final BitSet poppedY, final int numOfBits) {
        for (int j = 0; j < numOfBits; j++) {
            if ((poppedX.get(j) == false)
                    || ((poppedX.get(j) == true) && (poppedY.get(j) == true))) {
                poppedX.set(j, true);
            } else {
                poppedX.set(j, false);
            }
        }
        return poppedX;
    }
    
    // BitSet also does not include an equivalence that tests each bit and sets each bit accordingly.
    public BitSet equivalence (final BitSet poppedX,
            final BitSet poppedY, final int numOfBits) {
        for (int j = 0; j < numOfBits; j++) {
            if (poppedX.get(j) == poppedY.get(j)) {
                poppedX.set(j, true);
            } else {
                poppedX.set(j, false);
            }
        }
        return poppedX;
    }
    
    // Insert values from input into the Map which gives them a corresponding bit value.
    public Map<Character, BitSet> buildMap (final String inputLine,
            final String connective) {
        final Map<Character, BitSet> props = new HashMap<Character, BitSet>();
        // Keep count of each proposition letter to control bit conversion.
        int propCount = 0;
        for (int i = 0; i < inputLine.length(); i++) {
            if (!connective.contains(inputLine.charAt(i) + "")) {
                final BitSet bitSet = new BitSet(NUM_OF_BITS);
                boolean setter = false;
                for (int j = 0; j < NUM_OF_BITS; j = j + (1 << propCount)) {
                    // Set bits in a range dependent on the current number of props. (e.g. set every other bit if adding the first prop).
                    if (setter) {
                        bitSet.set(j, j + (1 << propCount));
                    }
                    setter = (setter) ? false : true;
                }
                // Insert character and bit into Map.
                props.put(inputLine.charAt(i), bitSet);
                propCount++;
            }
        }
        return props;
    }
    
    // Check the character that is being received as input to evaluate the bits.
    public final ArrayDeque<BitSet> evaluate (final ArrayDeque<BitSet> bits,
            final char input) {
        BitSet poppedX, poppedY;
        switch (input) {
        // Negate
        case '-':
            poppedX = bits.pop();
            poppedX.flip(0, NUM_OF_BITS);
            bits.push(poppedX);
            break;
        // And operation
        case '&':
            poppedX = bits.pop();
            poppedY = bits.pop();
            poppedX.and(poppedY);
            bits.push(poppedX);
            break;
        // Or operation
        case '|':
            poppedX = bits.pop();
            poppedY = bits.pop();
            poppedX.or(poppedY);
            bits.push(poppedX);
            break;
        // Equivalence operation
        case '=':
            poppedX = bits.pop();
            poppedY = bits.pop();
            bits.push(equivalence(poppedX, poppedY, NUM_OF_BITS));
            break;
        // Implies operation
        case '>':
            poppedX = bits.pop();
            poppedY = bits.pop();
            bits.push(implication(poppedX, poppedY, NUM_OF_BITS));
            break;
        // Space or any other characters.
        default:
            break;
        }
        return bits;
    }

    // Added for ease of the user so that this class could later be used with other classes, if need be.
    public static void main (final String[] args) {
        new Checker();
    }

    public Checker() {
        final Scanner input = new Scanner(System.in, "US-ASCII");
        ArrayDeque<BitSet> bits = new ArrayDeque<BitSet>();
        final String connective = "-&|=> ";
        while (input.hasNextLine()) {
            final String inputLine = input.nextLine();
            final Map<Character, BitSet> props = buildMap(inputLine, connective);
            // Iterate through characters in the input string.
            for (int i = 0; i < inputLine.length(); i++) {
                if (i < inputLine.length() && !connective.contains(inputLine.charAt(i) + "")) {
                    final BitSet newBits = (BitSet) props.get(
                            inputLine.charAt(i)).clone();
                    // Push bit representation onto stack.
                    bits.push(newBits);
                }
                bits = evaluate(bits, inputLine.charAt(i));
            }
            // Final pop off stack.
            final BitSet finalBits = bits.pop();
            /*
             * Check bits of final to see if it is always true, always false, or
             * a mixture of both.
             */
            if (finalBits.cardinality() == NUM_OF_BITS) {
                System.out.println("tautology");
            } else if (finalBits.cardinality() == 0) {
                System.out.println("contradiction");
            } else {
                System.out.println("contingent");
            }
        }
        input.close();
    }
}

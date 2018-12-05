import java.io.IOException;

/**
 * Authors: Cody Clark, Lily Pollreis, Marcel Baier, Stephen Booth, Christna Eder
 * Date last modified: Nov 6, 2018
 *
 * This project implements an ANDTree search in order to find the optimum assignment of classes to possible slots.
 * The algorithm used was based on a branch-and-bound algorithm that chooses the most optimal current leaf in a tree
 * of possible assignments and then expands that leaf, repeating this process until the best solution is found. 
 */

public class Project {

    public static int pen_section = 5;
    public static int pen_coursemin = 2;
    public static int pen_labsmin = 2;
    public static int pen_notpaired = 4;


    public static void main(String[] args) throws IOException {
        if(args.length == 5) {
            pen_coursemin = Integer.parseInt(args[1]);
            pen_labsmin = Integer.parseInt(args[2]);
            pen_notpaired = Integer.parseInt(args[3]);
            pen_section = Integer.parseInt(args[4]);
        }

        Parser parser = new Parser(args[0]);

        SearchControl searchControl = parser.parseInput();

        Assignment best = searchControl.searchOptimum();

        if(best == null) {
            System.out.println("No solution was found!");
        } else {
            System.out.println(best.getPrintableSolution());
        }
        // Parse input - create instances of course and lab objects that correspond to the items parsed in
        // We need to decide how we're storing the hard and soft constraints
        // Instantiate classes i.e. create instances of necessary objects

        // Run search - Use Atree branch and bound search algorithm
        // Search all of the possible assignments for the one with the lowest Eval value, pursue that branch
        // Until we find a complete assign that has an Eval-value lower than every other option
        // We only create branches that do not violate Constr

        // Output result - See example output text file for formatting

    }
}

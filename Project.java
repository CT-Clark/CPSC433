/**
 * Authors: Cody Clark, Lily Pollreis, Marcel Baier, Stephen Booth, Christna Eder
 * Date last modified: Nov 6, 2018
 *
 * (Fill in project description here)
 */

public class Project {
  
  // Function that tests whether an assignment of courses is valid
  // Takes in an Assign class instance
  public boolean Constr(Assign assignment) {
    bool result;
    
    return result; 
  }
  
  // Measures the soft constraints of an assignment
  // Takes in an assign class instance
  public int Eval(Assign assignment) {
    int result;
    
    return result; 
  }
  
  public static void main(String[] args) {
  // Instantiate classes i.e. create instances of list objects with null values
    
  // Parse input - create instances of course and lab objects that correspond to the items parsed in
  // We need to decide how we're storing the hard and soft constraints
  // We could pass them to Eval and Constr as a list?
  
  // Run search - Use Atree branch and bound search algorithm
  // Search all of the possible assignments for the one with the lowest Eval value, pursue that branch
  // Until we find a complete assign that has an Eval-value lower than every other option
  // We only create branches that do not violate Constr
    
  // Output result - See example output text file for formatting

  }
}

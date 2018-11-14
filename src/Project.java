/**
 * Authors: Cody Clark, Lily Pollreis, Marcel Baier, Stephen Booth, Christna Eder
 * Date last modified: Nov 6, 2018
 *
 * (Fill in project description here)
 */

public class Project {
  // Search control
  public Assignment kontrol(Assignment a) {
    return a;
  }
  
  // Note on constraints: All of the constraints will be stored within the Lecture objects
  
  // Function that tests whether an assignment of courses is valid
  // Takes in an Assignment class instance
  public boolean Constr(Assignment a) {
    boolean result;
    
    return false;
  }
  
  // Measures the soft constraints of an assignment
  // Takes in an assignment class instance
  public int Eval(Assignment a) {
    int result = 0;
    
    return result; 
  }
  
  public static void main(String[] args) {
  
    
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

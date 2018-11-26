/**
 * Authors: Cody Clark, Lily Pollreis, Marcel Baier, Stephen Booth, Christna Eder
 * Date last modified: Nov 6, 2018
 *
 * (Fill in project description here)
 */

public class Project {
  // Search control
  public Assignment kontrol(Assignment assign) {
    return a;
  }
  
  // Note on constraints: All of the constraints will be stored within the Lecture objects
  
  // Function that tests whether an assignment of courses is valid
  // Takes in an Assignment class instance
  public boolean Constr(Assignment assign) {
    boolean result;
    // Test that not more than courseMax courses are assigned to a slot
    // Test that not more than labMax labs are assigned to a slot
    // Labs cannot be scheduled at the same time as courses
    // Test that non-compatible courses are not assigned together
    // The final assignment correctly uses the partial assignment parsed into it
    // Test that unwanted courses are not assigned to their unwanted slots
    // Test that courses assigned to a Monday slot are also assigned to a We and Fr slot
    // If a course is assigned to a slot on Tuesday, it also has to be assigned to the respective Thu slot
    // Test that all LEC 9 section number courses are slotted into evening slots
    // Test that all 500 level courses are assigned to different slots
    // No courses can be scheduled on Tuesday 11:00-12:30
    // Test that CPSC 813 and CPSC 913 are scheduled Tu/Th 18:00-19:00 and CPSC 313 and CPSC 413 coursesélabs are not
    // scheduled during or overlapping these times
    
    return result;
  }
  
  // Measures the soft constraints of an assignment
  // Takes in an assignment class instance
  public int Eval(Assignment assign) {
    int result = 0;
    
    result += pen_labmin;
    result += pen_coursemin;
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

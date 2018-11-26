import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SearchControl {
    private TreeSet<Lecture> orderedLectures;
    private List<Slot> courseSlots;
    private List<Slot> labSlots;
    private List<Assignment> partialSolutions;
    private Assignment best;
    private int boundValue;
    private List<Pair> not_compatible;
    private List<Pair> pairs;

    public SearchControl(List<Lecture> lectures, List<Slot> courseSlots, List<Slot> labSlots, List<Pair> not_compatible, List<Pair> pairs) {
        orderedLectures = new TreeSet<Lecture>((Lecture x, Lecture y) -> y.constraintCount > x.constraintCount ? 1 : -1);
        this.courseSlots = courseSlots;
        this.labSlots = labSlots;
        this.not_compatible = not_compatible;
        this.pairs = pairs;
        establishVirtualSlots();
        countConstraints(lectures);
    }

    public void countConstraints(List<Lecture> lectures) {
        for(Pair pair : not_compatible) {
            lectures.get(lectures.indexOf(pair.a)).incrementConstraints();
            lectures.get(lectures.indexOf(pair.b)).incrementConstraints();
        }
        orderedLectures.addAll(lectures);
    }

    // See which lab and course slots are overlapping (e.g. Tu/Th)
    public void establishVirtualSlots() {
        for(Slot slot : courseSlots) {
            for(Slot labSlot : labSlots) {
                slot.testOverlapping(labSlot);
            }

        }

        for(Slot slot : labSlots) {
            for(Slot courseSlot : courseSlots) {
                slot.testOverlapping(courseSlot);
            }
        }
    }

    // The actual search control
    // This generates new leafs, and then from the selection of new leafs, chooses which is the best to expand
    // Will continue doing so until it reaches a finished leaf with a value which is better than all of the other
    // valid assignments
    public Assignment searchOptimum() {
        best = new Assignment(courseSlots, labSlots, orderedLectures.stream().collect(Collectors.toList()));
        partialSolutions.add(best); // ** Is this to track the path of the solution? **

        while(true) {
            // Tests if finished (i.e. not unassigned courses/labs)
            if(best.finished()) {
                    return best;
            }

            // Generate the new leafs
            List<Assignment> newPartialSolutions = best.generateNewLeafs(orderedLectures.first());
            partialSolutions.addAll(newPartialSolutions);
            orderedLectures.remove(orderedLectures.first());

            for(Assignment singleAssignment : newPartialSolutions) {
               if(singleAssignment.finished()) {
                   singleAssignment.evalValue = eval(singleAssignment);
               }
            }

            Assignment newBest = partialSolutions.get(0);
            for(Assignment singleAssign : partialSolutions) {
                if(singleAssign.evalValue < newBest.evalValue) {
                    newBest = singleAssign;
                }
            }
            boundValue = newBest.evalValue;
        }
    }  // ** Note on constraints: All of the constraints will be stored within the Lecture objects **
  
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
  public int eval(Assignment assign) {
    int result = assignment.evalValue;

    //TODO: Add minimum per slot penalty for full solutions
    // ** As per the website, it mentions that the penalties will be provided
    // yet on the sample inputs they are not. Are we providing our own? **
    for(Slot slot : assignment.labSlots) {

    }

    for(Slot slot : assignment.courseSlots) {

    }
    
    result += pen_labmin;
    result += pen_coursemin;
    return result; 
  }
}

// Object which represent the state of the assignment - from a partial assignment to the final answer
// Contains all of the Slot objects and their partical assignments
import java.util.ArrayList;
import java.util.List;

public class Assignment {
  List<Slot> courseSlots;
  List<Slot> labSlots;
  List<Lecture> unassignedLectures;
  int evalValue;

  public Assignment(List<Slot> courseSlots, List<Slot> labSlots, List<Lecture> unassignedLectures) {
      this.courseSlots = courseSlots;
      this.labSlots = labSlots;
      this.unassignedLectures = unassignedLectures;
      evalValue = 0;
  }

  public Assignment(Assignment assignment) {
        courseSlots = new ArrayList<>(assignment.courseSlots.size());
        courseSlots.addAll(assignment.courseSlots);
        labSlots = new ArrayList<>(assignment.labSlots.size());
        labSlots.addAll(assignment.labSlots);
        unassignedLectures = new ArrayList<>(assignment.unassignedLectures.size());
        unassignedLectures.addAll(assignment.unassignedLectures);
  }

  public boolean finished() {
      return unassignedLectures.isEmpty();
  }

  public void removeAssignedLecture(Lecture lec) {
      unassignedLectures.remove(lec);
  }

  public List<Assignment> generateNewLeafs(Lecture lectureToBeAssigned) {
      List<Assignment> newLeafs= new ArrayList<>();
      //deep copy actual assignment
      if (lectureToBeAssigned instanceof Course){
          for(Slot slot : courseSlots) {
              Assignment newAssign = new Assignment(this);
              Slot newSlot = slot.assignLecture(lectureToBeAssigned);
              newAssign.evalValue += newSlot.getEvalScore() - slot.getEvalScore();
              newAssign.courseSlots.remove(slot);
              newAssign.courseSlots.add(newSlot);

              if(newSlot.checkHardConstraints(lectureToBeAssigned, courseSlots, labSlots)){
                  newLeafs.add(newAssign);
              }
          }
      } else {
          for (Slot slot : labSlots) {
              Assignment newAssign = new Assignment(this);
              Slot newSlot = slot.assignLecture(lectureToBeAssigned);
              newAssign.evalValue += newSlot.getEvalScore() - slot.getEvalScore();
              newAssign.labSlots.remove(slot);
              newAssign.labSlots.add(newSlot);
              if(newSlot.checkHardConstraints(lectureToBeAssigned, courseSlots, labSlots)){
                  newLeafs.add(newAssign);
              }
          }

      }
      //try to put lectureToBeAssigned in each slot
      //check each new solution if it breaks a hard constraint
        //check if any slot has to much courses
        //check if there are forbidden overlaps
      return newLeafs;
  }

}

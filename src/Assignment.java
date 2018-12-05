// Object which represent the state of the assignment - from a partial assignment to the final answer
// Contains all of the Slot objects and their partical assignments
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Assignment {
  Collection<Slot> courseSlots;
  Collection<Slot> labSlots;
  List<Lecture> unassignedLectures;
  int evalValue;
  int minimalEvalValueToGet;

  public Assignment(Collection<Slot> courseSlots, Collection<Slot> labSlots, List<Lecture> unassignedLectures) {
      this.courseSlots = courseSlots;
      this.labSlots = labSlots;
      this.unassignedLectures = unassignedLectures;
      evalValue = 0;
      minimalEvalValueToGet = 0;
  }

  public Assignment(Collection<Slot> courseSlots, Collection<Slot> labSlots) {
      this.courseSlots = courseSlots;
      this.labSlots = labSlots;
      this.unassignedLectures = new ArrayList<>();
      evalValue = 0;
      minimalEvalValueToGet = 0;
  }

  public Assignment(Assignment assignment) {
        evalValue = assignment.evalValue;
        courseSlots = new ArrayList<>(assignment.courseSlots.size());
        courseSlots.addAll(assignment.courseSlots);
        labSlots = new ArrayList<>(assignment.labSlots.size());
        labSlots.addAll(assignment.labSlots);
        unassignedLectures = new ArrayList<>(assignment.unassignedLectures.size());
        unassignedLectures.addAll(assignment.unassignedLectures);
  }

  public void estimateMinimalMinimalPenalty() {
      minimalEvalValueToGet = 0;

      int neededCourses = 0;
      for(Slot slot : courseSlots) {
          int neededCoursesInSlot = slot.getMin() - slot.getAssignedLectures().size();
          if(neededCoursesInSlot > 0) {
              neededCourses += neededCoursesInSlot;
          }
      }

      int neededLabs = 0;
      for(Slot slot : labSlots) {
          int neededLabsInSlot = slot.getMin() - slot.getAssignedLectures().size();
          if(neededLabsInSlot > 0) {
              neededLabs += neededLabsInSlot;
          }
      }

      int availableCourses = 0;
      int availableLabs = 0;
      for(Lecture lec : unassignedLectures) {
          if(lec instanceof  Course) {
              availableCourses++;
          } else {
              availableLabs++;
          }
      }

      if(availableCourses < neededCourses) {
          minimalEvalValueToGet += Project.pen_coursemin * (neededCourses - availableCourses);
      }
      if(availableLabs < neededLabs) {
          minimalEvalValueToGet += Project.pen_labsmin * (neededLabs - availableLabs);
      }
  }

  public boolean finished() {
      return unassignedLectures.isEmpty();
  }

  public void removeAssignedLecture(Lecture lec) {
      unassignedLectures.remove(lec);
  }

  public List<Assignment> generateNewLeafs(/*Lecture lectureToBeAssigned*/) {
      List<Assignment> newLeafs= new ArrayList<>();
      //deep copy actual assignment
      Lecture  lectureToBeAssigned = unassignedLectures.iterator().next();
      unassignedLectures.remove(0);
      //unassignedLectures.remove(lectureToBeAssigned);
      if (lectureToBeAssigned instanceof Course){
          for(Slot slot : courseSlots) {
              Assignment newAssign = new Assignment(this);
              Slot newSlot = slot.assignLecture(lectureToBeAssigned);
              newSlot.evaluateSoftConstraints(lectureToBeAssigned, courseSlots, labSlots, unassignedLectures);
              newAssign.evalValue += newSlot.getEvalScore() - slot.getEvalScore();
              newAssign.estimateMinimalMinimalPenalty();
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
              newSlot.evaluateSoftConstraints(lectureToBeAssigned, courseSlots, labSlots, unassignedLectures);
              newAssign.evalValue += newSlot.getEvalScore() - slot.getEvalScore();
              newAssign.estimateMinimalMinimalPenalty();
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

  public Assignment assignLecture(Lecture lec, Slot slot) {
      Assignment newAssign = new Assignment(this);

      Iterator<Lecture> lecIt = unassignedLectures.iterator();
      while(lecIt.hasNext()){
          Lecture lecture = lecIt.next();
          if(lec.equals(lecture)) {
              lec = lecture;
              break;
          }
      }

      Iterator<Slot> slotIt;
      if(lec instanceof Course) {
          slotIt = courseSlots.iterator();
      } else {
          slotIt = labSlots.iterator();
      }

      while(slotIt.hasNext()){
          Slot itSlot = slotIt.next();
          if(slot.equals(itSlot)) {
              slot = itSlot;
              break;
          }
      }

      Slot newSlot = slot.assignLecture(lec);
      newSlot.evaluateSoftConstraints(lec, courseSlots, labSlots, unassignedLectures);
      newAssign.evalValue += newSlot.getEvalScore() - slot.getEvalScore();
      if(slot.getType() == GeneralSlot.COURSE) {
        newAssign.courseSlots.remove(slot);
        newAssign.courseSlots.add(newSlot);
      } else {
          newAssign.labSlots.remove(slot);
          newAssign.labSlots.add(newSlot);
      }
      newAssign.unassignedLectures.remove(lec);
      if(!newSlot.checkHardConstraints(lec, courseSlots, labSlots)){
          return null;
      }
      return newAssign;
  }

  public String getPrintableSolution() {
      TreeMap<String, String> allLectures = new TreeMap<>();

      for(Slot courseSlot : courseSlots) {
          List<Lecture> lectures = courseSlot.getAssignedLectures();
          Map<String, String> map;
          map = lectures.stream().map(l -> l.getId())
                  .collect(Collectors.toMap(Function.identity(), l -> courseSlot.getId()));
          allLectures.putAll(map);
      }

      for(Slot labSlot : labSlots) {
          List<Lecture> lectures = labSlot.getAssignedLectures();
          Map<String, String> map;
          map = lectures.stream().map(l -> l.getId())
                  .collect(Collectors.toMap(Function.identity(), l -> labSlot.getId()));
          allLectures.putAll(map);
      }

      StringBuffer buff = new StringBuffer();
      buff.append("Eval-value: " + evalValue + "\n");

      for(Map.Entry<String, String> pair : allLectures.entrySet()) {
          switch(pair.getKey().length() - 15) {
              case 0: buff.append(pair.getKey() + "\t\t\t: " + pair.getValue() + "\n");
                break;
              default: buff.append(pair.getKey() + "\t: " + pair.getValue() + "\n");
                break;
          }

      }

      return buff.toString();
  }
}

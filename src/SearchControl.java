import java.util.*;
import java.util.stream.Collectors;

public class SearchControl {
    private TreeSet<Lecture> orderedLectures;
    private HashMap<String, Slot> courseSlots;
    private HashMap<String, Slot> labSlots;
    private PriorityQueue<Assignment> partialSolutions;
    private Assignment best;
    private int boundValue;

    public SearchControl(List<Lecture> lectures, HashMap<String, Slot> courseSlots, HashMap<String, Slot> labSlots, Assignment s0) {
        orderedLectures = new TreeSet<Lecture>((Lecture x, Lecture y) -> y.constraintCount > x.constraintCount ? 1 : -1);
        this.courseSlots = courseSlots;
        this.labSlots = labSlots;

        establishVirtualSlots();
        countEveningSlots(lectures);
        addForbiddenSlotForCPSCCourses(lectures);

        addSameSectionIncompatibilities(lectures);
        add500LevelCourseIncompatibilities(lectures);

        partialSolutions = new PriorityQueue<>((Assignment x, Assignment y) ->
        {
            if((x.evalValue + x.minimalEvalValueToGet) > (y.evalValue + y.minimalEvalValueToGet)) {
                return 1;
            } else if ((x.evalValue + x.minimalEvalValueToGet) < (y.evalValue + y.minimalEvalValueToGet)) {
                return -1;
            } else {
                if(x.unassignedLectures.size() > y.unassignedLectures.size()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        //Load partial assignments
        if(s0 != null) {
            best = s0;
            lectures = s0.unassignedLectures;
            best.unassignedLectures = new ArrayList<>(lectures);
        } else {
            best = new Assignment(courseSlots.values(), labSlots.values(), lectures);
        }

        //Put in CPSC 813 / 913
        addCPSCNotCompatible(lectures);
        lectures = best.unassignedLectures;
        countConstraints(lectures);
        best.unassignedLectures = new ArrayList<>(orderedLectures);

    }

    public void add500LevelCourseIncompatibilities(List<Lecture> lectures) {
        for(Lecture lec : lectures) {
            for(Lecture lec2 : lectures) {
                if(lec instanceof Course && lec.department.equals("CPSC") && lec.number >= 500 && lec.number < 600){
                    if(lec2 instanceof Course && lec2.department.equals("CPSC") && lec2.number >= 500 && lec.number < 600) {
                        if(lec.number != lec2.number) {
                            lec.addNotCompatible(lec2);
                        }
                    }
                }
            }
        }
    }

    public void addSameSectionIncompatibilities(List<Lecture> lectures) {
        for(Lecture lec : lectures) {
            for(Lecture lec2 : lectures) {
                //The corresponding labs of one section should not be held at the same time
                if(lec instanceof Course){
                    if(lec2 instanceof Lab) {
                        if(lec.department.equals(lec2.department) && lec.number == lec2.number &&
                        lec.section == ((Lab)lec2).getCourseSection()) {
                            lec.addNotCompatible(lec2);
                        }
                    }
                }
                //The corresponding course of one section should not be held at the same time
                if(lec instanceof Lab) {
                    if(lec2 instanceof Course) {
                        if(lec.department.equals(lec2.department) && lec.number == lec2.number &&
                                ((Lab)lec).getCourseSection() == lec2.section) {
                            lec.addNotCompatible(lec2);
                        }
                    }
                }
            }
        }
    }

    public void addCPSCNotCompatible(List<Lecture> lectures) {
        List<Lecture> course313 = new ArrayList<>();
        List<Lecture> course413 = new ArrayList<>();
        List<Lecture> course813 = new ArrayList<>();
        List<Lecture> course913 = new ArrayList<>();

        for(Lecture singleLec : lectures) {
            if(singleLec.department.equals("CPSC") && singleLec.number == 313) {
                course313.add(singleLec);
            }
            if(singleLec.department.equals("CPSC") && singleLec.number == 413){
                course413.add(singleLec);
            }
            if(singleLec.department.equals("CPSC") && singleLec.number == 813){
                course813.add(singleLec);
            }
            if(singleLec.department.equals("CPSC") && singleLec.number == 913){
                course913.add(singleLec);
            }
        }

        if(course313.isEmpty() && course413.isEmpty()){
            return;
        }

        Slot blockedSlot = labSlots.get("TU,1800," + !GeneralSlot.COURSE);
        if(blockedSlot == null) {
            if(!course813.isEmpty() || !course913.isEmpty()) {
                throw new IllegalStateException("Course CPSC 813 or 913 could not be assigned to the predefined slot!");
            }
        }

        for(Lecture lec813sec : course813) {
            for(Lecture course313sec : course313) {
                lec813sec.addNotCompatible(course313sec);
                course313sec.addNotCompatible(lec813sec);

                for(Lecture correspondingLab : course313sec.not_compatible) {
                   //if(correspondingLab instanceof Lab) {
                   //   Lab correspondingLab2 = (Lab) correspondingLab;
                    //if(correspondingLab2.department.equals(course313sec.department) &&
                    //        correspondingLab2.number == course313sec.number &&
                    //correspondingLab2.getCourseSection() == course313sec.section) {
                        correspondingLab.addNotCompatible(lec813sec);
                        lec813sec.addNotCompatible(correspondingLab);
                    //}
                   //}
                }
            }


            if(!course313.isEmpty()) {
            for(Lecture transativeForbidden : course313.get(0).not_compatible) {
                if(transativeForbidden instanceof Course) {
                    lec813sec.addNotCompatible(transativeForbidden);
                    transativeForbidden.addNotCompatible(lec813sec);
                }
            }
            }
            best = best.assignLecture(lec813sec, blockedSlot);

        }

        for(Lecture lec913sec : course913) {
            for(Lecture course413sec : course413) {
                lec913sec.addNotCompatible(course413sec);
                course413sec.addNotCompatible(lec913sec);

                for(Lecture correspondingLab : course413sec.not_compatible) {
                    //if(correspondingLab instanceof Lab) {
                    //    Lab correspondingLab2 = (Lab) correspondingLab;
                    //    if(correspondingLab2.department.equals(course413sec.department) &&
                     //           correspondingLab2.number == course413sec.number &&
                      //          correspondingLab2.getCourseSection() == course413sec.section) {
                            correspondingLab.addNotCompatible(lec913sec);
                            lec913sec.addNotCompatible(correspondingLab);
                     //   }
                    //}
                }
            }

            if(!course413.isEmpty()) {
                for (Lecture transativeForbidden : course413.get(0).not_compatible) {
                    if (transativeForbidden instanceof Course) {
                        lec913sec.addNotCompatible(transativeForbidden);
                        transativeForbidden.addNotCompatible(lec913sec);
                    }
                }
            }
            best = best.assignLecture(lec913sec, blockedSlot);
        }

    }

    public void countEveningSlots(List<Lecture> lectures) {
        List<Slot> nonEveningSlots = new ArrayList<>();
        for(Slot singleSlot : courseSlots.values()) {
            if(singleSlot.getStartTime() < 1800) {
                nonEveningSlots.add(singleSlot);
            }
        }

        for(Lecture singleLec : lectures) {
            if (singleLec instanceof Course && singleLec.department.equals("CPSC") && singleLec.section >= 90) {
                for(Slot forbiddenSlot : nonEveningSlots) {
                    singleLec.addUnwanted(forbiddenSlot);
                }
            }
        }
    }

    public void addForbiddenSlotForCPSCCourses(List<Lecture> lectures) {
        Slot blockedSlot = courseSlots.get("TU,1100," + GeneralSlot.COURSE);
        if(blockedSlot == null) {
            return;
        }

        for(Lecture singleLec : lectures) {
            if(singleLec instanceof Course && (singleLec.department.equals("CPSC") 
            		|| singleLec.department.equals("SENG"))) {
                singleLec.addUnwanted(blockedSlot);
            }
        }
    }

    public void countConstraints(List<Lecture> lectures) {
        orderedLectures.addAll(lectures);
    }

    public void establishVirtualSlots() {
        for(Slot slot : courseSlots.values()) {
            for(Slot labSlot : labSlots.values()) {
                slot.testOverlapping(labSlot);
            }

        }

        for(Slot slot : labSlots.values()) {
            for(Slot courseSlot : courseSlots.values()) {
                slot.testOverlapping(courseSlot);
            }
        }
    }

    public Assignment searchOptimum() {
        partialSolutions.add(best);

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = 0;
        long allocatedMemory = 0;
        long freeMemory = 0;

        while(true) {
            maxMemory = runtime.maxMemory();
            allocatedMemory = runtime.totalMemory();
            freeMemory = runtime.freeMemory();

            if(maxMemory == allocatedMemory) {
                //Running out of memory, only 10MByte left
                if(freeMemory < 10485760) {
                    Assignment bestsofar = partialSolutions.poll();
                    return bestsofar;
                }
            }
            if(partialSolutions.peek().finished()) {
                    return partialSolutions.peek();
            }

            List<Assignment> newPartialSolutions = partialSolutions.poll().generateNewLeafs();

            for(Assignment singleAssignment : newPartialSolutions) {
                if(singleAssignment.finished()) {
                    singleAssignment.evalValue = eval(singleAssignment);
                }
            }

            partialSolutions.addAll(newPartialSolutions);

            if(partialSolutions.size() == 0) {
                System.err.println("No solution could be found, which adheres to all hard constraints!");
                return null;
            }
            boundValue = partialSolutions.peek().evalValue;
        }
    }

    public int eval(Assignment assignment) {
        int eval = assignment.evalValue;

        for(Slot slot : assignment.labSlots) {
            if (slot.getAssignedLectures().size() < slot.getMin()) {
                eval += Project.pen_labsmin * Project.w_minfilled;
            }
        }

        for(Slot slot : assignment.courseSlots) {
            if (slot.getAssignedLectures().size() < slot.getMin()) {
                eval += Project.pen_coursemin * Project.w_minfilled;
            }
        }

        return eval;
    }

}

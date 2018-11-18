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

    public Assignment searchOptimum() {
        best = new Assignment(courseSlots, labSlots, orderedLectures.stream().collect(Collectors.toList()));
        partialSolutions.add(best);

        while(true) {
            if(best.finished()) {
                    return best;
            }

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
    }

    public int eval(Assignment assignment) {
        int eval = assignment.evalValue;

        //TODO: Add minimum per slot penalty for full solutions
        for(Slot slot : assignment.labSlots) {

        }

        for(Slot slot : assignment.courseSlots) {

        }

        return 0;
    }


}

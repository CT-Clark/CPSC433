import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssignedSlot extends Slot {
    private List<Lecture> assigned;
    private Slot slot;
    private int evalScore;

    public AssignedSlot(Slot slot, Lecture lec) {
        this.slot = slot;
        this.evalScore = slot.getEvalScore();
        this.assigned = new ArrayList<>();

        assigned.addAll(slot.getAssignedLectures());
        assigned.add(lec);
    }

    public boolean checkHardConstraints(Lecture lec, List<Slot> courseSlots, List<Slot> labSlots) {
        //TODO: Course and Lab not at the same time
        //Unwanted check
        if(slot.getUnwanted().contains(lec)){
            return false;
        }

        //Maximum
        if(assigned.size() > slot.getMax()) {
            return false;
        }

        //Not-Compatible
        List<Slot> overlaps = new ArrayList<>();
        for(Slot slot : slot.getOverlappingSlots()){
            overlaps.add(courseSlots.indexOf(slot) == -1 ? labSlots.get(labSlots.indexOf(slot)) : courseSlots.get(courseSlots.indexOf(slot)));
        }

        List<Lecture> lectures = new ArrayList<>();
        for(Slot slot : overlaps) {
            lectures.addAll(slot.getAssignedLectures());
        }

        for(Lecture lecture : lectures) {
            Pair newPair = new Pair(lec, lecture);
            if(slot.getNotPaired().contains(newPair)) {
            return false;
            }
        }
        return true;
    }

    @Override
    public List<Pair> getNotPaired() {
        return slot.getNotPaired();
    }

    public int evaluateSoftConstraints(Lecture lec) {
        //Check preferences
        if(lec.preferedSlots.indexOf(this) == -1) {
            evalScore += lec.preferenceScore;
        }
        //TODO: Slot minimum
        //TODO: Course Section and Lab Section not on same time
        //TODO: paired check
        return evalScore;
    }

    public List<Lecture> getAssignedLectures() {
        return assigned;
    }

    public Slot assignLecture(Lecture lec) {
        return new AssignedSlot(this, lec);
    }

    @Override
    public Day getDay() {
        return slot.getDay();
    }

    @Override
    public int getStartTime() {
        return slot.getStartTime();
    }

    @Override
    public int getMin() {
        return slot.getMin();
    }

    @Override
    public int getMax() {
        return slot.getMax();
    }

    @Override
    public int getDuration() {
        return slot.getDuration();
    }

    @Override
    public boolean getType() {
        return slot.getType();
    }

    @Override
    public List<Lecture> getUnwanted() {
        return slot.getUnwanted();
    }


    public boolean equals(Object o) {
        return slot.equals(o);
    }

    public int getEvalScore() {
        return evalScore;
    }

    @Override
    public HashMap<Lecture, Integer> getPreferences() {
        return slot.getPreferences();
    }

    @Override
    public void saveUnwantedLecture(Lecture lec1) {
        slot.saveUnwantedLecture(lec1);
    }

    @Override
    public void testOverlapping(Slot slot) {
        slot.testOverlapping(slot);
    }

    @Override
    public List<Slot> getOverlappingSlots() {
        return slot.getOverlappingSlots();
    }
}


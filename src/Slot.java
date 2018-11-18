import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Slot {

    public Slot() {

    }

    public abstract Slot assignLecture(Lecture lec);
    public abstract List<Lecture> getAssignedLectures();

    public abstract Day getDay();
    public abstract int getStartTime();
    public abstract int getMin();
    public abstract int getMax();
    public abstract int getDuration();
    public abstract boolean getType();
    public abstract List<Lecture> getUnwanted();
    public abstract int getEvalScore();
    public abstract HashMap<Lecture, Integer> getPreferences();

    public abstract void saveUnwantedLecture(Lecture lec1);
    public abstract void testOverlapping(Slot slot);
    public abstract List<Slot> getOverlappingSlots();
    public abstract boolean checkHardConstraints(Lecture lec, List<Slot> courseSlots, List<Slot> labSlots);
    public abstract List<Pair> getNotPaired();

    //public abstract boolean checkHardConstraints(Lecture lec);
    //public abstract int evaluateSoftConstraints(Lecture lec);
}

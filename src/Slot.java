import java.util.ArrayList;
import java.util.Collection;
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
    public abstract String getId();

    public abstract void saveUnwantedLecture(Lecture lec1);
    public abstract void testOverlapping(Slot slot);
    public abstract List<Slot> getOverlappingSlots();
    public abstract boolean checkHardConstraints(Lecture lec, Collection<Slot> courseSlots, Collection<Slot> labSlots);

    public abstract boolean overlappingLectureSections(Lecture lec);
    public abstract int evaluateSoftConstraints(Lecture lec, Collection<Slot> courseSlots, Collection<Slot> labSlots, Collection<Lecture> unassignedLectures);

}

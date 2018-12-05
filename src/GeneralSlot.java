import java.util.*;

// Static knowledge about each slot
public class GeneralSlot extends Slot {
    public static final boolean COURSE = true;
    private Day day; // One of: MO, TU, FR
    private int startTime; // e.g. 8:00, 11:30
    private int min;
    private int max;
    private int duration; // One of: 60, 90, 120
    private boolean type;
    private List<Lecture> unwanted;
    private HashMap<Lecture, Integer> preferences;
    private List<Slot> overlappingSlots;

    public GeneralSlot(String slotDatum, int parseInt, boolean type) {
        unwanted = new ArrayList<>();
        preferences = new LinkedHashMap<>();
        this.day = Day.valueOf(slotDatum);
        this.type = type;
        overlappingSlots = new ArrayList<>();

        startTime = parseInt;

        if (type == COURSE) {
            if (this.day == Day.MO) {
                duration = 100;
            } else {
                duration = 130;
            }
        } else {
            if (this.day == Day.MO || this.day == Day.TU) {
                duration = 100;
            } else {
                duration = 200;
            }
        }
    }

    public GeneralSlot(String day, int time, int max, int min, boolean type){
        this(day, time, type);
        this.max = max;
        this.min = min;

        if(max < min || min < 0) {
            throw new IllegalStateException("Corrupted input data in Course slots!");
        }

        System.out.println(toString());
    }

    public GeneralSlot(GeneralSlot slot) {
        day = slot.day;
        startTime = slot.startTime;
        min = slot.min;
        max = slot.max;
        duration = slot.duration;
        type = slot.type;
        unwanted = slot.unwanted;
        preferences = slot.preferences;

    }

    public boolean overlap(GeneralSlot slot) {
        if((slot.getDay() == day) ||
                (slot.day == Day.FR && day == Day.MO) ||
                (slot.day == Day.MO && day == Day.MO)){
            if (slot.getStartTime() >= startTime && slot.getStartTime() < (startTime + duration)) {
                overlappingSlots.add(slot);
                return true;
            }
            if (slot.getStartTime() + slot.getDuration() >= startTime){
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuffer string = new StringBuffer();
        string.append("SLOT: ");
        string.append("Type: " + (type ? "Course" : "Lab"));
        string.append("; Day: " + day);
        String time = String.valueOf(startTime);
        string.append("; Time: " + time.substring(0, time.length() - 2) + ":" + time.substring(time.length() - 2, time.length()));
        string.append("; Duration: " + duration);
        string.append("; Max: " + max + "; Min: " + min);
        return string.toString();
    }

    public static GeneralSlot produceSlot(String representation, boolean type) {
        representation = representation.replace(" ", "");
        String[] slotData = representation.split(",");
        if (slotData.length != 4) {
            return new GeneralSlot(slotData[0], Integer.parseInt(slotData[1].replaceAll(":", "")), type);
        }

        return new GeneralSlot(slotData[0], Integer.parseInt(slotData[1].replaceAll(":", "")),
                Integer.parseInt(slotData[2]), Integer.parseInt(slotData[3]), type);
    }

    public boolean equals(Object o) {
        if(o instanceof Slot){
            Slot slot = (Slot) o;
            if(day == slot.getDay() &&
                    startTime == slot.getStartTime() &&
                    duration == slot.getDuration() &&
                    type == slot.getType()
            ) {
                return true;
            }
        }
        return false;
    }

    public void testOverlapping(Slot slot) {
        if((day == Day.MO && slot.getDay() == Day.FR) || day == slot.getDay()) {
            if ((slot.getStartTime() <= startTime && (slot.getStartTime() + slot.getDuration()) > startTime) ||
                    (startTime + duration > slot.getStartTime() && startTime <= slot.getStartTime())) {
                //vSlot.addOverlapping(slot);
                overlappingSlots.add(slot);
            }
        }
    }

    public Slot assignLecture(Lecture lec) {
        return new AssignedSlot(this, lec);
    }

    public List<Lecture> getAssignedLectures() {
        return new ArrayList<>();
    }

    public List<Slot> getOverlappingSlots() {
        return overlappingSlots;
    }

    @Override
    public boolean checkHardConstraints(Lecture lec, Collection<Slot> courseSlots, Collection<Slot> labSlots) {
        return true;
    }
    
    @Override
    public boolean overlappingLectureSections(Lecture lec) {
        return false;
    }

    @Override
    public int evaluateSoftConstraints(Lecture lec, Collection<Slot> courseSlots, Collection<Slot> labSlots, Collection<Lecture> unassignedLectures) {
        return 0;
    }

    @Override
    public Day getDay() {
        return day;
    }

    @Override
    public int getStartTime() {
        return startTime;
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean getType() {
        return type;
    }

    @Override
    public List<Lecture> getUnwanted() {
        return unwanted;
    }

    public int getEvalScore() {
        return 0;
    }

    @Override
    public HashMap<Lecture, Integer> getPreferences() {
        return preferences;
    }

    @Override
    public String getId() {
        String time = String.valueOf(startTime);
        if(time.length() == 4) {
            return day.name() + ", " + time.substring(0,2) + ":" + time.substring(2);
        } else {
            return day.name() + ", " + time.substring(0,1) + ":" + time.substring(1);
        }
    }

    @Override
    public void saveUnwantedLecture(Lecture lec1) {
        unwanted.add(lec1);
        lec1.constraintCount++;
    }

}

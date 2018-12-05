import java.util.*;

public class AssignedSlot extends Slot {
    private List<Lecture> assigned;
    private Slot slot;
    private int evalScore;

    public AssignedSlot(Slot slot, Lecture lec) {
        if(slot instanceof AssignedSlot) {
            this.slot = ((AssignedSlot)slot).slot;
        } else {
            this.slot = slot;
        }
        this.evalScore = slot.getEvalScore();
        this.assigned = new ArrayList<>();

        assigned.addAll(slot.getAssignedLectures());
        assigned.add(lec);
    }

    public boolean overlappingLectureSections(Lecture lec) {
        for(Lecture assignedLec : assigned) {
            if (assignedLec.sameSection(lec) && !lec.equals(assignedLec)) {
                return true;
            }
        }
        return false;
    }

    public boolean overlappingLectures(Lecture lec) {

        for(Lecture assignedLec : assigned) {
            if (assignedLec.sameModule(lec) && !lec.equals(assignedLec)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkHardConstraints(Lecture lec, Collection<Slot> courseSlots, Collection<Slot> labSlots) {
        //Maximum
        if(assigned.size() > slot.getMax()) {
            return false;
        }

        //Unwanted check
        if(lec.getUnwanted().contains(this)){
            return false;
        }

        for(Slot overlappingSlot : slot.getOverlappingSlots()) {
            if(overlappingSlot.overlappingLectureSections(lec)){
                return false;
            }
        }

        //Not-Compatible
        List<Slot> overlaps = new ArrayList<>();
        for(Slot slot : slot.getOverlappingSlots()){
            if(courseSlots.contains(slot)) {
                Iterator<Slot> cSlotIt = courseSlots.iterator();
                while(cSlotIt.hasNext()) {
                    Slot cPos = cSlotIt.next();
                    if(cPos.equals(slot)) {
                        overlaps.add(cPos);
                    }
                }
            } else {
                Iterator<Slot> lSlotIt = labSlots.iterator();
                while(lSlotIt.hasNext()) {
                    Slot lPos = lSlotIt.next();
                    if(lPos.equals(slot)) {
                        overlaps.add(lPos);
                    }
                }
            }
        }

        List<Lecture> lectures = new ArrayList<>();
        for(Slot slot : overlaps) {
            lectures.addAll(slot.getAssignedLectures());
        }
        lectures.addAll(assigned);


        for(Lecture lecture : lectures) {
            if(lec.not_compatible.contains(lecture) && !lec.equals(lecture)) {
                return false;
            }
        }
        return true;
    }

    public int evaluateSoftConstraints(Lecture lec, Collection<Slot> courseSlots, Collection<Slot> labSlots, Collection<Lecture> unasssignedLectures) {
        //Check preferences
        if(lec.preferedSlots.indexOf(this) == -1) {
            evalScore += lec.preferenceScore * Project.w_pref;
        }

        //Check if different sections overlap
        if(lec.department.equals("CPSC")){
        if(overlappingLectures(lec)) {
            evalScore += Project.pen_section;
        }}

        for(Slot overlappingSlot : slot.getOverlappingSlots()) {
            if(overlappingSlot.overlappingLectureSections(lec)){
                evalScore += Project.pen_section * Project.w_secdiff;
            }
        }

        //Unwanted
        List<Slot> overlaps = new ArrayList<>();
        for(Slot slot : slot.getOverlappingSlots()){
            if(courseSlots.contains(slot)) {
                Iterator<Slot> cSlotIt = courseSlots.iterator();
                while(cSlotIt.hasNext()) {
                    Slot cPos = cSlotIt.next();
                    if(cPos.equals(slot)) {
                        overlaps.add(cPos);
                    }
                }
            } else {
                Iterator<Slot> lSlotIt = labSlots.iterator();
                while(lSlotIt.hasNext()) {
                    Slot lPos = lSlotIt.next();
                    if(lPos.equals(slot)) {
                        overlaps.add(lPos);
                    }
                }
            }
        }

        List<Lecture> lectures = new ArrayList<>();
        for(Slot slot : overlaps) {
            lectures.addAll(slot.getAssignedLectures());
        }


        for(Lecture lecture : lec.pair) {
            //Already assigned
            if(!unasssignedLectures.contains(lecture)) {
                //But not in this slot
                if(!lectures.contains(lecture)) {
                    evalScore += Project.pen_notpaired * Project.w_pair;
                }
            }
        }
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
    public String getId() {
        return slot.getId();
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

    @Override
    public String toString() {
        return slot.toString();
    }
}


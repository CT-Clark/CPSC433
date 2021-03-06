import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Lecture {
    protected String department; // CPSC/SENG
    protected int number; // e.g. 433
    protected int section; // e.g. 1, 3
    protected List<Slot> preferedSlots;
    protected int preferenceScore;
    protected int constraintCount;

    public Lecture(String department, int number, int section) {
        preferedSlots = new ArrayList<>();
        if(department.length() != 4) {
            throw new IllegalArgumentException("Department acronym must have always four letters!");
        }
        this.department = department;
        this.number = number;
        this.section = section;
    }

    public void incrementConstraints() {
        constraintCount++;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Empty input!");
        }
        this.department = s;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int s) {
        if (s > 0) {
            this.number = s;
        } else {
            throw new IllegalArgumentException("Number must be above zero!");
        }
    }

    public int getSection() {
        return section;
    }

    public void setSection(int s) {
        if (s > 0) {
            this.section = s;
        } else {
            throw new IllegalArgumentException("Section must be above zero!");
        }
    }

    public String toString() {
        StringBuffer string = new StringBuffer();
        string.append("Department: " + department);
        string.append("; Number: " + number);
        string.append("; Section: " + section);
        return string.toString();
    }

    public boolean equals(Object lecture){
        if(lecture instanceof  Lecture){
            Lecture lec = (Lecture) lecture;
            if(department.equals(lec.department) &&
            number == lec.number &&
            section == lec.section) {
                return true;
            }
        }
        if (lecture instanceof String) {
            return equals(produce((String)lecture));
        }
        return false;
    }

    protected abstract Lecture produce(String representation);

    public static Lecture produceLecture(String representation) {
        if(representation.contains("TUT")){
            return Lab.produceLab(representation);
        } else {
            return Course.produceCourse(representation);
        }
    }

    // TODO: Getter and setter methods for the sets of eval attributes

    // TODO: Assign function - figure out how we're storing already assigned course/lab, unassigned,
    // and how to go about accessing that data easily
}

public class Lecture {
    protected String department; // CPSC/SENG
    protected int number; // e.g. 433
    protected int section; // e.g. 1, 3
    protected Slot slot;


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

    // TODO: Getter and setter methods for the sets of eval attributes

    // TODO: Assign function - figure out how we're storing already assigned course/lab, unassigned,
    // and how to go about accessing that data easily
}

public class Lab extends Lecture {
    private static int NOT_ASSIGNED_TO_LECTURE_SECTION = 0;
    private int courseSection;

    public Lab(String department, int number, int courseSection, int section) {
        super(department, number, section);
        this.courseSection = courseSection;
        System.out.println(toString());
    }

    public Lab(String department, int number, int section) {
        super(department, number, section);
        courseSection = NOT_ASSIGNED_TO_LECTURE_SECTION;
        System.out.println(toString());
    }

    public String toString() {
        return "LAB: " + super.toString() + " ; LecSection: " + (courseSection == NOT_ASSIGNED_TO_LECTURE_SECTION ? "undefined" : courseSection);
    }

    public boolean equals(Object lab) {
        if(lab instanceof  Lab) {
            return super.equals(lab) && ((Lab) lab).courseSection == courseSection;
        }
        return false;
    }
}

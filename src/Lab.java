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
        if(lab instanceof String) {
            equals(produceLab((String) lab));
        }
        return false;
    }

    protected  Lecture produce(String representation) {return produceLab(representation);}

    public static Lab produceLab(String representation) {
        representation = representation.trim();
        String[] courseData = representation.split("\\s");
        if (courseData.length == 4){
            return new Lab(courseData[0], Integer.parseInt(courseData[1]), Integer.parseInt(courseData[3]));
        } else if (courseData.length == 6) {
            return new Lab(courseData[0], Integer.parseInt(courseData[1]), Integer.parseInt(courseData[3]), Integer.parseInt(courseData[5]));
        }
        throw new IllegalStateException();
    }
}

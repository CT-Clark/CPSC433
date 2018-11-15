public class Course extends Lecture {

    public Course(String department, int number, int section) {
        super(department, number, section);
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "COURSE: " + super.toString();
    }
}

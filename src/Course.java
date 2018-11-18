public class Course extends Lecture {

    public Course(String department, int number, int section) {
        super(department, number, section);
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "COURSE: " + super.toString();
    }

    protected Lecture produce(String representation) {
        return produceCourse(representation);
    }

    public static Course produceCourse(String representation) {
        representation = representation.trim();
        String[] courseData = representation.split("\\s");
        if (courseData.length != 4){
            throw new IllegalStateException("Each course should be defined by four attributes!");
        }
        return new Course(courseData[0], Integer.parseInt(courseData[1]), Integer.parseInt(courseData[3]));
    }
}

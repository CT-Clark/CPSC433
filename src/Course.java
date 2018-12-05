public class Course extends Lecture {

    public Course(String department, int number, int section) {
        super(department, number, section);
    }

    @Override
    public boolean sameSection(Lecture lec) {
        if(department.equals(lec.department) && number == lec.number) {
        if(lec instanceof  Course) {
            return section == lec.section;
        } else {
            return section == ((Lab)lec).getCourseSection();
        }
        }
        return false;
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
        representation = representation.replaceAll(" +", " ");
        String[] courseData = representation.split("\\s");
        if (courseData.length != 4){
            throw new IllegalStateException("Each course should be defined by four attributes!");
        }
        return new Course(courseData[0], Integer.parseInt(courseData[1]), Integer.parseInt(courseData[3]));
    }

    public String getId() {
        String id = department + " " + number + " LEC ";
        if(section < 10) {
            id += "0" + section;
        }  else {
            id += section;
        }
        return id;
    }

    public boolean equals(Object lecture){
        if(lecture instanceof Course){
            Course lec = (Course) lecture;
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
}

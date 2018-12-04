package structures;

// This class specifically defines a course object, which is a version of the class superclass object
// This is specifically for "lectures"

public class Course extends Class {
	
	public Course(String dept, int id, int lecture, int rank) {
		super(dept, id, lecture, rank);
	}
	
	public Course(Course course) {
		super(course);
	}
	
	@Override
	public Object clone() {
		return new Course(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Course)) {
			return false;
		}
		
		Course c = (Course) o;
		
		return getDept().compareTo(c.getDept()) == 0 && Integer.compare(getId(), c.getId()) == 0 && Integer.compare(getLecture(), c.getLecture()) == 0;
	}
		
	@Override
	public String getType() {
		return "course";
	}
	
}

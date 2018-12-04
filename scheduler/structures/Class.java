package structures;

/**
 * This class defines a superclass for the course and lab objects
 */

public class Class implements Cloneable, Comparable<Class> {

	private String dept;
	private int id;
	private int lecture;
	private int rank;
	
	public Class(String dept, int id, int lecture, int rank) {
		this.dept = dept;
		this.id = id;
		this.lecture = lecture;
		this.rank = rank;
	}
	
	
	public Class(String dept, int id, int lecture) {
		this.dept = dept;
		this.id = id;
		this.lecture = lecture;
		this.setRank(0);
	}

	// Clones a class
	public Class(Class newClass) {
		this.dept = newClass.dept;
		this.id = newClass.id;
		this.lecture = newClass.lecture;
		this.rank = newClass.rank;
	}
	
	@Override
	public Object clone() {
		return new Class(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Class)) {
			return false;
		}
		
		Class c = (Class) o;
		
		return dept.compareTo(c.dept) == 0 && Integer.compare(id, c.id) == 0 && Integer.compare(lecture, c.lecture) == 0 && Integer.compare(rank, c.rank) == 0;
	}
	
	@Override
	public int compareTo(Class c) {
		return this.rank - c.rank;
	}
	
	public String getDept() { return dept; }
	
	public void setDept(String dept) { this.dept = dept; }
	
	public int getId() { return id; }
	
	public void setId(int id) { this.id = id; }
	
	public int getLecture() { return lecture; }
	
	public void setLecture(int lecture) { this.lecture = lecture; }
	
	public String getType() { return null; }
	
	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getLectureString() {
		if (lecture == -1) {
			return " ";
		}
		
		String retStr = lecture < 10 ? "0" + String.valueOf(lecture) : String.valueOf(lecture); 
		
		return " LEC " + retStr + " ";
	}
	
	public String getTutorialString() {
		return "";
	}

}

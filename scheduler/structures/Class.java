package structures;

public class Class implements Cloneable {

	private String dept;
	private int id;
	private int lecture;
	
	public Class(String dept, int id, int lecture) {
		this.dept = dept;
		this.id = id;
		this.lecture = lecture;
	}
	
	public Class() {
		this.dept = null;
	}
	
	public Class(Class newClass) {
		this.dept = newClass.dept;
		this.id = newClass.id;
		this.lecture = newClass.lecture;
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
		
		return dept.compareTo(c.dept) == 0 && Integer.compare(id, c.id) == 0 && Integer.compare(lecture, c.lecture) == 0;
	}
	
	public String getDept() {
		return dept;
	}
	
	public void setDept(String dept) {
		this.dept = dept;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getLecture() {
		return lecture;
	}
	
	public void setLecture(int lecture) {
		this.lecture = lecture;
	}
	
	public String getType() {
		return null;
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

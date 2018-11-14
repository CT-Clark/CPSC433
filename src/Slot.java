import java.util.List;

// Slot object, to which courses are assigned

public class Slot {
  private String type; // Lab/Course
  private String day; // One of: MO, TU, WE, TH, FR
  private int startTime; // e.g. 8:00, 11:30
  private int courseMax;
  private int courseMin;

  // The list of courses and labs assigned to this slot
  private List<Object> assigned = new ArrayList<Object>();
  
  public String getType() {
    return type;
  }
  
  public String getDay() {
    return day;
  }
  
  public int getStartTime() {
    return startTime;
  }
  
  public int getCourseMax() {
    return courseMax;
  }
  
  public int getCourseMin() {
    return courseMin;
  }
  
  public void setType(String s) {
    this.type = s;
  }
  
  public void setDay(String s) {
    this.day = s;
  }
  
  public void setStartTime(int i) {
    this.startTime = i;
  }
  
  public void setCourseMin(int i) {
    this.courseMin = i;
  }
  
  public void setCourseMax(int i) {
    this.courseMax = i;
  }
}

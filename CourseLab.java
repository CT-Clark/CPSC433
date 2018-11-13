import java.util.List;

public class CourseLab {
  private String department; // CPSC/SENG
  private String number; // e.g. 433
  private String lectureNumber; // So that we know which lecture slot this corresponds to
  private String labNumber; // e.g. 01, 03
  // List of set of not compatible, unwanted, pairs, preferences
  
  public String getDepartment() {
    return department;
  }
  
  public String getNumber() {
    return number;
  }
  
  public String getLectureNumber() {
    return lectureNumber;
  }

  public String getLabNumber() {
    return labNumber;
  }
  
  public void setDepartment(String s) {
    this.department = s;
  }
  
  public void setNumber(String s) {
    this.number = s;
  }
  
  public void setLectureNumber(String s) {
    this.lectureNumber = s;
  }
  
  public void setLabnumber(String s) {
    this.labNumber = s;
  }
}

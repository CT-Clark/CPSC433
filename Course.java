public class Course {
  private String department; // CPSC or SENG
  private String number; // e.g. 433
  private String lectureNumber; // e.g. 01 or 02
  
  public String getDepartment() {
    return department;
  }
  
  public String getNumber() {
    return number;
  }
  
  public String getLectureNumber() {
    return lectureNumber;
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
}

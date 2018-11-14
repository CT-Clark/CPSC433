import java.util.ArrayList;
import java.util.List;

// Slot object, to which courses are assigned

public class Slot {
    public static final boolean COURSE = true;
  private Day day; // One of: MO, TU, FR
  private int startTime; // e.g. 8:00, 11:30
  private int min;
  private int max;
  private int duration; // One of: 60, 90, 120
    private boolean type;

  // The list of courses and labs assigned to this slot
  private List<Lecture> assigned = new ArrayList<>();

  public Slot(String day, int time, int max, int min, boolean type){
      this.day = Day.valueOf(day);
      this.max = max;
      this.min = min;
      this.type = type;

      if(max < min || min < 0) {
          throw new IllegalStateException("Corrupted input data in Course slots!");
      }
      startTime = time;

      if (type == COURSE) {
          if (this.day == Day.MO) {
              duration = 60;
          } else {
              duration = 90;
          }
      } else {
          if (this.day == Day.MO || this.day == Day.TU) {
              duration = 60;
          } else {
              duration = 120;
          }
      }
      System.out.println(toString());
  }

  public Day getDay() {
    return day;
  }
  
  public int getStartTime() {
    return startTime;
  }
  
  public int getMax() {
    return max;
  }
  
  public int getMin() {
    return min;
  }

  public int getDuration( ) {return duration;}
  
  public void setDay(Day s){

  }
  
  public void setStartTime(int i) {
    this.startTime = i;
  }
  
  public void setMin(int i) {
    this.min = i;
  }
  
  public void setMax(int i) {
    this.max = i;
  }

  public boolean overlap(Slot slot) {
    //Missing friday
    if(slot.getDay() == day){
    if (slot.getStartTime() >= startTime && slot.getStartTime() < (startTime + duration)) {
      return true;
    }
    if (slot.getStartTime() + slot.getDuration() >= startTime){
      return true;
    }}
    return false;
  }

  public String toString() {
      StringBuffer string = new StringBuffer();
      string.append("Type: " + (type ? "Course" : "Lab"));
      string.append("; Day: " + day);
      String time = String.valueOf(startTime);
      string.append("; Time: " + time.substring(0, time.length() - 2) + ":" + time.substring(time.length() - 2, time.length()));
      string.append("; Duration: " + duration);
      string.append("; Max: " + max + "; Min: " + min);
      return string.toString();
  }
}

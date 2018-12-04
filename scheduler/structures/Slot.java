package structures;

import java.time.LocalTime;

// This class defines what a slot can be, the methods and fields
// Classes are not stored directly to a slot object, but rather are stored as a pair with the slot object

public class Slot implements Cloneable, Comparable<Slot> {
	
	private String type;
	private String day;
	private LocalTime startTime;
	private LocalTime endTime;
	private int max;
	private int min;
	private int rank;
	
	public Slot(String type, String day, LocalTime startTime, int max, int min, int rank) {
		this.type = type;
		this.day = day;
		this.startTime = startTime;
		
		if (day.equals("MO") || (day.equals("TU") && type.equals("lab"))) {
			this.endTime = startTime.plusMinutes(60);
		} else if (day.equals("TU")) {
			this.endTime = startTime.plusMinutes(90);
		} else {
			this.endTime = startTime.plusMinutes(120);
		}
		
		this.max = max;
		this.min = min;
		
		this.rank = rank;
	}
	
	public Slot(String type, String day, LocalTime startTime, int max, int min) {
		this.type = type;
		this.day = day;
		this.startTime = startTime;
		
		if (day.equals("MO") || (day.equals("TU") && type.equals("lab"))) {
			this.endTime = startTime.plusMinutes(60);
		} else if (day.equals("TU")) {
			this.endTime = startTime.plusMinutes(90);
		} else {
			this.endTime = startTime.plusMinutes(120);
		}
		
		this.max = max;
		this.min = min;
		
		this.setRank(0);
	}
	
	public Slot() {
		this.type = null;
		this.day = null;
		this.startTime = null;
		this.endTime = null;
		this.max = -1;
		this.min = -1;
		this.setRank(-1);
	}
	
	public Slot(Slot slot) {
		this.type = slot.getType();
		this.day = slot.getDay();
		this.startTime = slot.getStartTime();
		this.endTime = slot.getEndTime();
		this.max = slot.getMax();
		this.min = slot.getMin();
		this.setRank(slot.getRank());
	}
	
	@Override
	public Object clone() {
		return new Slot(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Slot)) {
			return false;
		}
		
		Slot s = (Slot) o;
		
		return type.compareTo(s.type) == 0 && day.compareTo(s.day) == 0 && startTime.compareTo(s.startTime) == 0 && endTime.compareTo(s.endTime) == 0 
				&& Integer.compare(max, s.max) == 0 && Integer.compare(min, s.min) == 0;
	}
	
	@Override
	public int compareTo(Slot s) {
		return this.rank - s.getRank();
	}

	@Override
	public String toString() {
		String ret = "";
		ret += this.day + " ";
		ret += this.startTime.toString() + " ";

		return ret;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}
	
	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
}

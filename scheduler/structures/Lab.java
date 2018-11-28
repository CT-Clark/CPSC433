package structures;

public class Lab extends Class {

	private int tutorial;
	
	public Lab(String dept, int id, int lecture, int tutorial) {
		super(dept, id, lecture);
		this.tutorial = tutorial;
	}
	
	public Lab(Lab lab) {
		super(lab);
		this.tutorial = lab.tutorial;
	}

	public int getTutorial() {
		return tutorial;
	}

	public void setTutorial(int tutorial) {
		this.tutorial = tutorial;
	}
	
	@Override
	public Object clone() {
		return new Lab(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Lab)) {
			return false;
		}
		
		Lab l = (Lab) o;
		
		return this.getDept().compareTo(l.getDept()) == 0 && Integer.compare(this.getId(), l.getId()) == 0 
				&& Integer.compare(this.getLecture(), l.getLecture()) == 0 && Integer.compare(tutorial, l.tutorial) == 0;
	}

	@Override
	public String getType() {
		return "lab";
	}
	
	@Override
	public String getTutorialString() {
		String retStr = tutorial < 10 ? "0" + String.valueOf(tutorial) : String.valueOf(tutorial);
			
		return "TUT " + retStr;
	}
	
}

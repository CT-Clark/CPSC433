package structures;

// This package defines a custom defined pair, which can hold any pair of objects

public class Pair<U, V> {
	
	private U first;
	private V second;
	
	public Pair(U first, V second) {
		this.first = first;
		this.second = second;
	}
	
	public Pair() {
		this.first = null;
		this.second = null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		Pair<?, ?> pair = (Pair<?, ?>) o;
		
		if (!first.equals(pair.first)) {
			return true;
		} else {
			return second.equals(pair.second);
		}
	}
	
	public U getFirst() {
		return first;
	}
	public void setFirst(U first) {
		this.first = first;
	}
	public V getSecond() {
		return second;
	}
	public void setSecond(V second) {
		this.second = second;
	}

	/*
	@Override
	public int compareTo(Pair<U, V> p) {
		int compFirst = this.getFirst().compareTo(p.getFirst());
		int compSecond = this.getSecond().compareTo(p.getSecond());
		
		if (compFirst == 0 && compSecond != 0) {
			return compSecond;
		} else if (compFirst != 0 && compSecond == 0) {
			 return compSecond;
		} else {			
			if (compFirst > 0 && compSecond < 0) {
				return 1;
			} else if (compFirst < 0 && compSecond > 0) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	*/
}

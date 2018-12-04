package comparators;

import java.util.Comparator;

import structures.Class;
import structures.Pair;
import structures.Slot;

public class SortUnwantedPairs implements Comparator<Pair<Class, Slot>> {

	@Override
	public int compare(Pair<Class, Slot> p1, Pair<Class, Slot> p2) {
		return p1.getFirst().compareTo(p2.getFirst());
	}



}

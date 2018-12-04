package comparators;

import java.util.Comparator;

import structures.Class;
import structures.Pair;

public class SortClassPairs implements Comparator<Pair<Class, Class>> {

	@Override
	public int compare(Pair<Class, Class> p1, Pair<Class, Class> p2) {
		int p1Rank = p1.getFirst().getRank() + p1.getSecond().getRank();
		int p2Rank = p2.getFirst().getRank() + p2.getSecond().getRank();
		
		return p1Rank - p2Rank;
	}
	
}

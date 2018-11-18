public class Pair {
    public Lecture a, b;

    public Pair(Lecture originalLec, Lecture originalLec2) {
        a = originalLec;
        b = originalLec2;

        System.out.print(toString());
    }

    public String toString() {
        return "NOT_COMPATIBLE: (" + a + ",\t" + b + ")";
    }

    public boolean equals(Object o) {
        if(o instanceof Pair) {
            Pair p = (Pair)o;

            if((a.equals(p.a) && b.equals(p.b)) ||(a.equals(p.b) && b.equals(p.a))) {
                return true;
            }
        }
        return false;
    }

}

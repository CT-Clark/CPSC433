public class Pair {
    private Lecture a, b;

    public Pair(Lecture originalLec, Lecture originalLec2) {
        a = originalLec;
        b = originalLec2;

        System.out.print(toString());
    }

    public String toString() {
        return "NOT_COMPATIBLE: (" + a + ",\t" + b + ")";
    }

}

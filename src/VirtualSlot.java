import java.util.ArrayList;
import java.util.List;

public class VirtualSlot {
    private Slot slot;
    private List<Slot> overlapping;
    private List<Pair> not_Compatible;
    private List<Pair> pair;

    public VirtualSlot(Slot slot) {
        this.slot = slot;
        not_Compatible = new ArrayList<>();
        pair = new ArrayList<>();
        overlapping = new ArrayList<>();
    }

    public void addOverlapping(Slot slot) {
        overlapping.add(slot);
    }

    public VirtualSlot(VirtualSlot vSlot, Slot slot) {
        this.slot = slot;
        not_Compatible = vSlot.not_Compatible;
        pair = vSlot.pair;
    }

    public List<Slot> getOverlapping() {
        return overlapping;
    }

    public List<Pair> getNotPaired() {
        return not_Compatible;
    }
    //Hard constraint check
    public boolean checkNot_Compatible() {
        return false;
    }

    //Soft constraint check
    public int checkPairing() {
        return 0;
    }
}

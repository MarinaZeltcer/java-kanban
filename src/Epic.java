import java.util.ArrayList;

public class Epic extends Task {
    public Epic(String name, String description) {
        super(name, description);
    }

   private ArrayList<Integer> subtaskId = new ArrayList<>();

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }
}

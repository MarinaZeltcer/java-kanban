public class Subtask extends Task {
    private Integer EpicId;

    public Subtask(String name, String description,Integer epicId) {
        super(name, description);
        EpicId = epicId;
    }

    public Integer getEpicId() {
        return EpicId;
    }
}
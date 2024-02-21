package servise;
import java.util.List;
import model.Task;

import java.util.ArrayList;


public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
    void remove(int id);
}

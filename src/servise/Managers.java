package servise;

import java.io.File;

public class Managers {

    public   static TaskManagerable getDefault() {
        return new FileBackedTaskManager(new File("resourses/task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
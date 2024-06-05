package servise;

import java.io.File;

public class Managers {

 sprint_9-solution-http-api
  public   static TaskManagerable getDefault() {

    public   static TaskManagerable getDefault() {
 main
        return new FileBackedTaskManager(new File("resourses/task.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
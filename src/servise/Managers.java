package servise;

public class Managers {

  public   static TaskManagerable getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

   public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}

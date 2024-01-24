package servise;

public class Managers {

    static TaskManagerable getDefault() {
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}

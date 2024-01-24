package servise;

import model.Task;

import java.util.ArrayList;



public class InMemoryHistoryManager implements HistoryManager {

    ArrayList<Task> browsingHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
     if(browsingHistory.size() >= 10){
         browsingHistory.remove(0);
     }browsingHistory.add(task);
        }



    @Override
    public ArrayList<Task> getHistory() {
        return browsingHistory;
    }
}

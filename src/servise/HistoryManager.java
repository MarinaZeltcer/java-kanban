package servise;
import java.util.List;
import model.Task;

/*
* Неиспользуемый импорт. От таких импортов лучше избавляться.
* Скоро у вас в ТЗ стиль кода будет проверяться на гитхаб автоматически, такой код система не будет принимать.
* Поэтому старайся следить за этим сразу.
* */
import java.util.ArrayList;


public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);
}

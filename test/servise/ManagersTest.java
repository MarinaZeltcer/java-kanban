package servise;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {


    TaskManagerable inMemoryTaskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    public void getDefaultTest() {
        Assertions.assertNotNull(inMemoryTaskManager);
    }

    @Test
    public void getDefaultHistoryTest() {
        Assertions.assertNotNull(historyManager);
    }
}
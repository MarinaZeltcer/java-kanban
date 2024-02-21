package servise;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    public InMemoryTaskManager taskManager;
    HistoryManager historyManager = Managers.getDefaultHistory();

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager(historyManager);
    }


    @Test
    public void createNewTasksTest() {
        createTenTasks();
        assertEquals(10, taskManager.tasks.size());
    }

        @Test
        public void createNewEpicsTest () {
            createTenEpics();
            assertEquals(10, taskManager.epics.size());
        }


        @Test
        public void createNewSubTaskTest () {
            createTenSubtasks();
            assertEquals(10, taskManager.subtasks.size());
        }


        @Test
        public void shouldBeEqualToEachOtherIfTheirIdIsEqual () {
            createTenTasks();
            Task task1 = taskManager.getTaskById(5);
            Task task2 = taskManager.tasks.get(5);
            assertNotNull(task1);
            assertNotNull(task2);
            assertEquals(task1, task2, "обьекты не совпадают");
        }


        @Test
        public void createNewSubTaskTestNonExistentEpic () {
            Integer invalidEpicId = -6;
            Subtask subtask = new Subtask("Собрать коробки", "С подписями", invalidEpicId);
            taskManager.createNewSubTask(subtask);
            assertTrue(taskManager.subtasks.isEmpty());
        }


        @Test
        public void getTaskByIdTest () {
            Task task1 = new Task("task", "taskDesc");
            taskManager.createNewTask(task1);
            Task task2 = taskManager.getTaskById(task1.getId());
            assertEquals(task1, task2, "обьекты не совпадают");
        }

        @Test
        public void getEpicByIdTest () {
            Epic epic1 = new Epic("epic", "epicDesc");
            taskManager.createNewEpic(epic1);
            Epic epic2 = taskManager.getEpicById(epic1.getId());
            assertEquals(epic1, epic2, "обьекты не совпадают");
        }


        @Test
        public void getSubtaskByIdTest () {
            Epic epic = new Epic("Поход.", "С палатками, на озеро.");
            taskManager.createNewEpic(epic);
            Subtask subtask1 = new Subtask("Взять гитару", "Аккустическую", epic.getId());
            taskManager.createNewSubTask(subtask1);
            Subtask subtask2 = taskManager.getSubtaskById(subtask1.getId());
            assertEquals(subtask1, subtask2, "обьекты не совпадают");
        }


        @Test
        public void addFewTasksInHistoryListTest () {
            createTenTasks();
            taskManager.getTaskById(1);
            assertEquals(1, taskManager.getHistory().size());
            taskManager.getTaskById(2);
            assertEquals(2, taskManager.getHistory().size());
        }
        

        // Вспомогательный метод. Просто создаю 10 задач
        private void createTenTasks () {
            for (int i = 0; i < 10; i++) {
                Task task = new Task("task" + i, "taskDesc" + i);
                taskManager.createNewTask(task);
            }
        }

        // Вспомогательный метод. Просто создаю 10 эпиков
        private void createTenEpics() {
            for (int i = 0; i < 10; i++) {
                Epic epic = new Epic("epic" + i, "epicDesc" + i);
                taskManager.createNewEpic(epic);
            }
        }

        // Вспомогательный метод. Просто создаю 10 подзадач
        private void createTenSubtasks() {
            createTenEpics();
            for (int i = 0; i < 10; i++) {
                Subtask subtask = new Subtask("subtask" + i, "subtaskDesc" + i, i + 1);
                taskManager.createNewSubTask(subtask);
            }
        }
    }

package httpTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servise.InMemoryTaskManager;
import servise.TaskManagerable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerPrioritizedTest {

    private final TaskManagerable manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskServerPrioritizedTest() {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTask();
        manager.removeAllSubTask();
        manager.removeAllEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedList() {
        // создаём задачи
        Task task1 = new Task("Task1", "Testing", Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Task2", "Testing", Duration.ofMinutes(10),
                LocalDateTime.now().minusMinutes(60));
        Task task3 = new Task("Task3", "Testing");
        task1 = manager.createNewTask(task1);
        task2 = manager.createNewTask(task2);
        task3 = manager.createNewTask(task3);
        Epic epic = new Epic("Epic", "Testing");
        epic = manager.createNewEpic(epic);
        Subtask subtask = new Subtask("Task1", "Testing", Duration.ofMinutes(10),
                LocalDateTime.now().minusMinutes(1000), epic.getId());
        subtask = manager.createNewSubTask(subtask);

        // получаем ответ от сервера
        HttpResponse<String> response = sendGetRequestAndGetResponse("http://localhost:8080/prioritized");

        // проверяем статус ответа
        assertNotNull(response);
        assertEquals(200, response.statusCode());

        // получаем сортированный список задач
        String prioritizedListJson = response.body();
        List<Task> prioritizedList = gson.fromJson(prioritizedListJson, new TypeToken<List<Task>>(){}.getType());

        // проверяем список задач
        assertNotNull(prioritizedList);
        assertEquals(4, prioritizedList.size());
        assertEquals(subtask.getName(), prioritizedList.get(0).getName());
        assertEquals(task2.getName(), prioritizedList.get(1).getName());
        assertEquals(task1.getName(), prioritizedList.get(2).getName());
        assertEquals(task3.getName(), prioritizedList.get(3).getName());
    }

    private HttpResponse<String> sendGetRequestAndGetResponse(String uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();
        try {
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при отправке запроса " + e);
        }
        return null;
    }
}

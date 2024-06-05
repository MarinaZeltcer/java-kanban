package httpTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import model.Epic;
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

public class HttpTaskServerHistoryTest {

    private final TaskManagerable manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskServerHistoryTest() {
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
    public void testGetHistory() {
        // создаём задачи
        Task task = new Task("Task", "Testing", Duration.ofMinutes(5), LocalDateTime.now());
        Epic epic = new Epic("Epic", "Testing");
        task = manager.createNewTask(task);
        epic = manager.createNewEpic(epic);
        // просматриваем задачи
        manager.getEpicById(epic.getId());
        manager.getTaskById(task.getId());
        // получаем историю
        HttpResponse<String> historyJson = sendGetRequestAndGetResponse("http://localhost:8080/history");
        assertNotNull(historyJson);
        // проверяем лист с историей
        List<Task> historyList = gson.fromJson(historyJson.body(), new TypeToken<List<Task>>(){}.getType());
        assertNotNull(historyList);
        assertEquals(2, historyList.size());
        assertEquals(epic.getName(), historyList.get(0).getName());
        assertEquals(task.getName(), historyList.get(1).getName());
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

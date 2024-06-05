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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerEpicsTest {

    private final TaskManagerable manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskServerEpicsTest() {
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
    public void testAddEpic() {
        // создаём эпик
        Epic epic = new Epic("Test", "Testing");
        // конвертируем его в JSON
        String taskJson = gson.toJson(epic);
        // отправляем post запрос и получаем ответ
        HttpResponse<String> postResponse = sendPostRequestAndGetResponse(taskJson, "http://localhost:8080/epics");
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(postResponse);
        assertEquals(201, postResponse.statusCode());
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals(epic.getName(), epicsFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpic() {
        // создаём эпик
        Epic epic = new Epic("Test", "Testing");
        // Сохраняем его в менеджер
        epic = manager.createNewEpic(epic);
        // отправляем get запрос и получаем ответ
        HttpResponse<String> getResponse = sendGetRequestAndGetResponse("http://localhost:8080/epics");
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(200, getResponse.statusCode());
        // получаем из тела ответа список задач
        List<Epic> epicsFromResponse = gson.fromJson(getResponse.body(), new TypeToken<List<Epic>>(){}.getType());
        // проверяем список эпиков
        assertNotNull(epicsFromResponse, "Задачи не возвращаются");
        assertEquals(1, epicsFromResponse.size(), "Некорректное количество задач");
        assertEquals(epic.getName(), epicsFromResponse.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpicById() {
        // создаём эпик
        Epic epic = new Epic("Test", "Testing");
        // Сохраняем его в менеджер
        epic = manager.createNewEpic(epic);
        // отправляем get запрос и получаем ответ
        HttpResponse<String> getResponse = sendGetRequestAndGetResponse("http://localhost:8080/epics?id=" + epic.getId());
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(200, getResponse.statusCode());
        // получаем из тела ответа список эпиков
        Epic epicFromResponse = gson.fromJson(getResponse.body(), Epic.class);
        // проверяем список задач
        assertNotNull(epicFromResponse, "Задачи не возвращаются");
        assertEquals(epic.getName(), epicFromResponse.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpicById() {
        // создаём эпик
        Epic epic = new Epic("Test", "Testing");
        // Сохраняем его в менеджер
        epic = manager.createNewEpic(epic);
        // проверяем, что эпик сохранилась в менеджер
        assertEquals(1, manager.getAllEpics().size());
        // отправляем delete запрос и получаем ответ
        HttpResponse<String> getResponse = sendDeleteRequestAndGetResponse("http://localhost:8080/epics?id=" + epic.getId());
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(201, getResponse.statusCode());
        // проверяем, что эпик был удален
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    public void testDeleteAllEpics() {
        // создаём эпики
        Epic epic1 = new Epic("Test1", "Testing");
        Epic epic2 = new Epic("Test2", "Testing");
        // Сохраняем их в менеджер
        manager.createNewEpic(epic1);
        manager.createNewEpic(epic2);
        // проверяем, что задачи сохранилась в менеджер
        assertEquals(2, manager.getAllEpics().size());
        // отправляем delete запрос и получаем ответ
        HttpResponse<String> getResponse = sendDeleteRequestAndGetResponse("http://localhost:8080/epics");
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(201, getResponse.statusCode());
        // проверяем, что задачи были удалены
        assertEquals(0, manager.getAllEpics().size());
    }

    private HttpResponse<String> sendPostRequestAndGetResponse(String body, String uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(uri))
                .build();
        try {
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при отправке запроса " + e);
        }
        return null;
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

    private HttpResponse<String> sendDeleteRequestAndGetResponse(String uri) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
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

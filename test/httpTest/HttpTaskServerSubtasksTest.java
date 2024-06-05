package httpTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import http.HttpTaskServer;
import model.Epic;
import model.Subtask;
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

public class HttpTaskServerSubtasksTest {

    private final TaskManagerable manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = HttpTaskServer.getGson();
    HttpClient client = HttpClient.newHttpClient();

    public HttpTaskServerSubtasksTest() {
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
    public void testAddSubtask() {
        // создаём эпик
        Epic epic = new Epic("Test", "Testing");
        epic = manager.createNewEpic(epic);
        // создаем подзадачу
        Subtask subtask = new Subtask("TestSub", "TestDesc", epic.getId());
        // конвертируем её в JSON
        String taskJson = gson.toJson(subtask);
        // отправляем post запрос и получаем ответ
        HttpResponse<String> postResponse = sendPostRequestAndGetResponse(taskJson, "http://localhost:8080/subtasks");
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(postResponse);
        assertEquals(201, postResponse.statusCode());
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals(subtask.getName(), subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtask() {
        // создаём эпик
        Epic epic = new Epic("Test", "Testing");
        epic = manager.createNewEpic(epic);
        // Создаем подзадачу
        Subtask subtask = new Subtask("TestSub", "TestDesc", epic.getId());
        subtask = manager.createNewSubTask(subtask);
        // отправляем get запрос и получаем ответ
        HttpResponse<String> getResponse = sendGetRequestAndGetResponse("http://localhost:8080/subtasks");
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(200, getResponse.statusCode());
        // получаем из тела ответа список подзадач
        List<Subtask> subtasksFromResponse = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>(){}.getType());
        // проверяем список подзадач
        assertNotNull(subtasksFromResponse, "Задачи не возвращаются");
        assertEquals(1, subtasksFromResponse.size(), "Некорректное количество задач");
        assertEquals(subtask.getName(), subtasksFromResponse.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtaskById() {
        // создаём эпик
        Epic epic = new Epic("Test", "Testing");
        epic = manager.createNewEpic(epic);
        // создаем подзадачу
        Subtask subtask = new Subtask("TestSub", "TestDesc", epic.getId());
        subtask = manager.createNewSubTask(subtask);
        // отправляем get запрос и получаем ответ
        HttpResponse<String> getResponse = sendGetRequestAndGetResponse("http://localhost:8080/subtasks?id=" + subtask.getId());
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(200, getResponse.statusCode());
        // получаем из тела ответа список подзадач
        Subtask subtaskFromResponse = gson.fromJson(getResponse.body(), Subtask.class);
        // проверяем список подзадач
        assertNotNull(subtaskFromResponse, "Задачи не возвращаются");
        assertEquals(subtask.getName(), subtaskFromResponse.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteSubtaskById() {
        // создаём эпик
        Epic epic = new Epic("Test", "Testing");
        epic = manager.createNewEpic(epic);
        // создаем подзадачу
        Subtask subtask = new Subtask("TestSub", "TestDesc", epic.getId());
        subtask = manager.createNewSubTask(subtask);
        // проверяем, что подзадача сохранилась в менеджер
        assertEquals(1, manager.getAllSubtasks().size());
        // отправляем delete запрос и получаем ответ
        HttpResponse<String> getResponse = sendDeleteRequestAndGetResponse("http://localhost:8080/subtasks?id=" + subtask.getId());
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(201, getResponse.statusCode());
        // проверяем, что подзадача была удалена
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    public void testDeleteAllSubtasks() {  //////////////////////////////
        // создаём эпики
        Epic epic = new Epic("Test1", "Testing");
        manager.createNewEpic(epic);
        // создаем подзадачи
        Subtask subtask1 = new Subtask("TestSub1", "TestDesc", epic.getId());
        Subtask subtask2 = new Subtask("TestSub2", "TestDesc", epic.getId());
        subtask1 = manager.createNewSubTask(subtask1);
        subtask2 = manager.createNewSubTask(subtask2);
        // проверяем, что подзадачи сохранилась в менеджер
        assertEquals(2, manager.getAllSubtasks().size());
        // отправляем delete запрос и получаем ответ
        HttpResponse<String> getResponse = sendDeleteRequestAndGetResponse("http://localhost:8080/subtasks");
        // проверяем, что ответ был получен с правильным кодом
        assertNotNull(getResponse);
        assertEquals(201, getResponse.statusCode());
        // проверяем, что подзадачи были удалены
        assertEquals(0, manager.getAllSubtasks().size());
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

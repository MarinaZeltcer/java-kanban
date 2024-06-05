package handler;


import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import model.Subtask;
import servise.TaskManagerable;


import java.io.IOException;


import java.nio.charset.StandardCharsets;


public class SubtasksHandler implements HttpHandler {

    private final TaskManagerable taskManager;

    public SubtasksHandler(TaskManagerable taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;


        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        switch (method) {
            case "GET":
                if (query == null) {
                    statusCode = 200;
                    response = HttpTaskServer.getGson().toJson(taskManager.getAllSubtasks());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask != null) {
                            response = HttpTaskServer.getGson().toJson(subtask);
                            statusCode = 200;
                        } else {
                            response = "Подзадача с данным id не найдена";
                            statusCode = 404;
                        }

                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Subtask subtask = HttpTaskServer.getGson().fromJson(bodyRequest, Subtask.class);

                    Subtask newSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getepicIds());
                    if (subtask.getDuration() != null) {
                        newSubtask.setDuration(subtask.getDuration());
                    }
                    if (subtask.getStartTime() != null) {
                        newSubtask.setStartTime(subtask.getStartTime());
                    }
                    Integer id = subtask.getId();
                    if (taskManager.getSubtaskById(id) != null) {
                        taskManager.updateSubtask(subtask);
                        statusCode = 200;
                        response = "Подзадача с id=" + id + " обновлена";
                    } else {
                        Subtask subtaskCreated = taskManager.createNewSubTask(newSubtask);
                        System.out.println("CREATED SUBTASK: " + subtaskCreated);
                        Integer idCreated = subtaskCreated.getId();
                        statusCode = 201;
                        response = "Создана подзадача с id=" + idCreated;
                    }
                } catch (JsonSyntaxException e) {
                    response = "Неверный формат запроса";
                    statusCode = 400;
                } catch (RuntimeException e) {
                    statusCode = 406;
                    response = e.getMessage();
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.removeAllSubTask();
                    statusCode = 200;
                    exchange.sendResponseHeaders(statusCode,-1);
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.removeSubTaskById(id);
                        statusCode = 200;
                        exchange.sendResponseHeaders(statusCode,-1);
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует необходимый параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат id";
                    }
                }
                break;
            default:
                statusCode = 400;
                response = "Некорректный запрос";
        }
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
    }
}
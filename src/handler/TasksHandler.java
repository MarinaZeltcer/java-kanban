package handler;


import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import model.Task;

import servise.TaskManagerable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class TasksHandler implements HttpHandler {
    private final TaskManagerable taskManagerable;

    public TasksHandler(TaskManagerable taskManagerable) {
        this.taskManagerable = taskManagerable;
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
                    response = HttpTaskServer.getGson().toJson(taskManagerable.getAllTasks());
                    System.out.println("GET TASKS: " + response);

                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Task task = taskManagerable.getTaskById(id);
                        if (task != null) {
                            response = HttpTaskServer.getGson().toJson(task);
                            statusCode = 200;
                        } else {
                            response = "Задача с данным id не найдена";
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
                    Task task = HttpTaskServer.getGson().fromJson(bodyRequest, Task.class);
                    Task newTask = new Task(task.getName(), task.getDescription());
                    if (task.getDuration() != null) {
                        newTask.setDuration(task.getDuration());
                    }
                    if (task.getStartTime() != null) {
                        newTask.setStartTime(task.getStartTime());
                    }
                    Integer id = task.getId();
                    if (taskManagerable.getTaskById(id) != null) {
                        taskManagerable.updateTask(task);
                        statusCode = 201;
                        response = "Задача с id=" + id + " обновлена";
                    } else {
                        Task taskCreated = taskManagerable.createNewTask(newTask);
                        System.out.println("CREATED TASK: " + taskCreated);
                        Integer idCreated = taskCreated.getId();
                        statusCode = 201;
                        response = "Создана задача с id=" + idCreated;
                    }
                } catch (JsonSyntaxException e) {
                    statusCode = 400;
                    response = "Неверный формат запроса";
                } catch (RuntimeException e) {
                    statusCode = 406;
                    response = "Задачи пересекаются";
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManagerable.removeAllTask();
                    statusCode = 201;
                    exchange.sendResponseHeaders(statusCode, -1);
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManagerable.removeTaskById(id);
                        statusCode = 201;
                        exchange.sendResponseHeaders(statusCode, -1);
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





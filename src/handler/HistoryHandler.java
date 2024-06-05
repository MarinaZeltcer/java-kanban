package handler;


import com.google.gson.Gson;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import http.HttpTaskServer;

import servise.TaskManagerable;

import java.io.IOException;



public class HistoryHandler implements HttpHandler {
    private final Gson gson = HttpTaskServer.getGson();

    private final TaskManagerable taskManager;

    public HistoryHandler(TaskManagerable taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode = 400;
        String response;
        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        if (method.equals("GET")) {
            statusCode = 200;
            response = gson.toJson(taskManager.getHistory());
        } else {
            response = "Некорректный запрос";

        }

        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
    }
}

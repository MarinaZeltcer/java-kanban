package http;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.sun.net.httpserver.HttpServer;
import handler.*;

import servise.Managers;
import servise.TaskManagerable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;


public class HttpTaskServer {

    private static final int PORT = 8080;
    private final TaskManagerable taskManagerable;
    private HttpServer httpServer;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer(TaskManagerable taskManagerable) {
        this.taskManagerable = taskManagerable;
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler(taskManagerable));
            httpServer.createContext("/epics", new EpicsHandler(taskManagerable));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManagerable));
            httpServer.createContext("/history", new HistoryHandler(taskManagerable));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManagerable));
        } catch (IOException e) {
            throw new RuntimeException("Невозможно создать HTTP сервер");
        }
        httpServer.start();
        System.out.println("HTTP сервер запущен на " + PORT + " порту");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP сервер остановлен на " + PORT + " порту");

    }

    public static void main(String[] args) throws IOException {
        TaskManagerable taskManagerable = Managers.getDefault();

        HttpTaskServer taskServer = new HttpTaskServer(taskManagerable);
        taskServer.start();


    }
}




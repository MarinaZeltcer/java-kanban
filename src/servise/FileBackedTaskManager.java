package servise;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    void save() {
        String firstStr = "id,type,name,status,description,epic\n";
        try (Writer fileWriter = new FileWriter("task.csv")) {

            fileWriter.write(firstStr);
            for (Task allTask : getAllTasks()) {
                fileWriter.write(allTask.toString() + "\n");
            }
            for (Epic allEpic : getAllEpics()) {
                fileWriter.write(allEpic.toString() + "\n");
            }
            for (Subtask allSubtask : getAllSubtasks()) {
                fileWriter.write(allSubtask.toString() + "\n");
            }
            fileWriter.write("\n");
            for (Task task : getHistory()) {
                fileWriter.write(task.getId() + ",");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла");
        }
    }

    private static Task fromString(String[] split) {
        Task task;
        if ("SUBTASK".equals(split[1])) {
            task = new Subtask(split[2], split[4], Integer.parseInt(split[7]));
        } else if ("EPIC".equals(split[1])) {
            task = new Epic(split[2], split[4]);
        } else {
            task = new Task(split[2], split[4]);
        }
        task.setId(Integer.parseInt(split[0]));
        task.setStatus(Status.valueOf(split[3]));
        task.setDuration(Duration.ofMinutes(Long.parseLong(split[5])));
        task.setStartTime(LocalDateTime.parse(split[6]));
        return task;
    }

    public static FileBackedTaskManager loadFromFile(String filename) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(new File(filename));

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line = br.readLine();
            line = br.readLine();

            while (!line.isBlank()) {

                String[] split = line.split(",");
                Task task = fromString(split);

                if ("SUBTASK".equals(split[1])) {
                    Subtask subtask = (Subtask) task;
                    manager.subtasks.put(task.getId(), subtask);
                    manager.prioritizedTasks.add(subtask);
                    Epic epic = manager.epics.get(subtask.getepicIds());
                    epic.addSubtask(subtask);
                }
                if ("EPIC".equals(split[1])) {
                    manager.epics.put(task.getId(), (Epic) task);
                }
                if ("TASK".equals(split[1])) {

                    manager.tasks.put(task.getId(), task);
                    manager.prioritizedTasks.add(task);
                }
                line = br.readLine();
            }
            line = br.readLine();
            String[] split = line.split(",");
            for (String str : split) {
                Integer key = Integer.parseInt(str);
                if (manager.tasks.containsKey(key)) {
                    manager.historyManager.add(manager.tasks.get(key));
                }
                if (manager.subtasks.containsKey(key)) {
                    manager.historyManager.add(manager.subtasks.get(key));
                }
                if (manager.epics.containsKey(key)) {
                    manager.historyManager.add(manager.epics.get(key));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }
        return manager;
    }


    @Override
    public Task createNewTask(Task task) {
        Task newTask = super.createNewTask(task);
        save();
        return newTask;
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task newTask = super.getTaskById(id);
        save();
        return newTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        Epic newEpic = super.createNewEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic newEpic = super.getEpicById(id);
        save();
        return newEpic;
    }

    @Override
    public Subtask createNewSubTask(Subtask subtask) {
        Subtask newSubtask = super.createNewSubTask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public void removeSubTaskById(Integer id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask newSubtask = super.getSubtaskById(id);
        save();
        return newSubtask;
    }

}

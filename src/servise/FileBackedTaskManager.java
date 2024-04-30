package servise;

import model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    protected void save() {
        String firstStr = "id,type,name,status,description,epic\n";
        try (Writer fileWriter = new FileWriter(file)) {

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
            task = new Subtask(split[2], split[4], Integer.parseInt(split[5]));
        } else if ("EPIC".equals(split[1])) {
            task = new Epic(split[2], split[4]);
        } else {
            task = new Task(split[2], split[4]);
        }
        task.setId(Integer.parseInt(split[0]));
        task.setStatus(Status.valueOf(split[3]));
        return task;
    }

    public static FileBackedTaskManager loadFromFile(String filename) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(),new File(filename));

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line=br.readLine();
            line = br.readLine();

            while (!line.isBlank()) {

                String[] split = line.split(",");
                Task task = fromString(split);
                Integer newId = Integer.parseInt(split[0]);
                if ("SUBTASK".equals(split[1])) {
                    Subtask subtask = (Subtask) task;
                    manager.subtasks.put(newId, subtask);
                    if (manager.getId() < newId) {
                        manager.setId(newId + 1);
                    }

                    Epic epic = manager.epics.get(subtask.getepicIds());
                    epic.addSubtask(subtask);
                }
                if ("EPIC".equals(split[1])) {
                    manager.epics.put(newId,(Epic) task);
                    if(manager.getId()<newId){
                        manager.setId(newId + 1);
                    }
                }
                if ("TASK".equals(split[1])) {
                    manager.tasks.put(newId,task);
                    if(manager.getId()<newId){
                        manager.setId(newId + 1);
                    }
                }
                line = br.readLine();
            }
            line = br.readLine();
            if (line!=null){
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
            }}
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

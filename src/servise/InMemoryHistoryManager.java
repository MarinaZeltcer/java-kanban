package servise;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;


public class InMemoryHistoryManager implements HistoryManager {
    public Node first;
    public Node last;
    public HashMap<Integer, Node> historyReferences = new HashMap<>();



    private void linkLast(Task task) {
        final Node oldLast = last;
        final Node newNode = new Node(last, task, null);
        last = newNode;
        if (oldLast != null) {
            oldLast.next = newNode;
        } else {
            first = newNode;
        }

    }


    private void removeNode(Node removedNode) {
        Node prevNode = removedNode.prev;//Получаю предыдущий:
        Node nextNode = removedNode.next;//Получаю следующий
        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            first = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            last = prevNode;
        }

    }

    ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node currentNode = first;
        while (currentNode != null) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }


    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
            historyReferences.put(task.getId(), last);
        }
    }


    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (historyReferences.containsKey(id)) {
            Node tempNode = historyReferences.get(id);
            removeNode(tempNode);
            historyReferences.remove(id);
        }
    }


    public static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

}
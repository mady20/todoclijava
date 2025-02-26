import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Task {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int id;
    private String datetime;
    private String title;
    private String description;
    private boolean completed;

    Task(int id, String title, String description) {
        this.id = id;
        this.datetime = LocalDateTime.now().format(formatter);
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String toString() {
        return id + "." + " " + datetime + "\t" + title + "\t" + (completed ? "completed" : "not completed");
    }

    public void markCompleted() {
        completed = true;
    }
}

class Todo {
    private static final String FILE_PATH = "tasks.txt";

    public static void createTask(String title, String description) {
        int id = getNextId();
        Task task = new Task(id, title, description);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(task.toString() + "\n");
        } catch (IOException e) {
            System.err.println("Error while writing to the file: " + e.getMessage());
        }
    }

    public static void listTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error while reading from the file: " + e.getMessage());
        }
    }

    public static void completeTask(int taskId) {
        List<Task> tasks = readTasks();
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                task.markCompleted();
                saveTasks(tasks);
                return;
            }
        }
        System.err.println("Task with ID " + taskId + " not found.");
    }

    private static int getNextId() {
        List<Task> tasks = readTasks();
        if (tasks.isEmpty()) {
            return 1;
        }
        return tasks.get(tasks.size() - 1).getId() + 1;
    }

    private static List<Task> readTasks() {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                int id = Integer.parseInt(parts[0].split("\\.")[0]);
                String title = parts[1];
                String description = parts[2];
                Task task = new Task(id, title, description);
                tasks.add(task);
            }
        } catch (IOException e) {
            System.err.println("Error reading tasks: " + e.getMessage());
        }
        return tasks;
    }

    private static void saveTasks(List<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    public static void deleteTask(int taskId){
        List<Task> tasks = readTasks();
        int index = -1;
        for(Task task : tasks){
            if(task.getId() == taskId){
                index = task.getId() - 1;
                break;
            }
        }
        if(index == -1){
            System.out.println("Error: Task with id " + taskId + " not found!");
        }

        if(index == (tasks.size() - 1)){
            tasks.remove(tasks.get(index));
            saveTasks(tasks);
            return;
        }
        tasks.remove(tasks.get(index));
        for(int i = index; i < tasks.size(); i++){
            tasks.get(i).setId(tasks.get(i).getId() - 1);
        }
        saveTasks(tasks);
        return;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("USAGE: give the command!");
            return;
        }

        switch (args[0]) {
            case "add":
                Scanner sc = new Scanner(System.in);
                System.out.println("Enter task title:");
                String title = sc.nextLine();
                System.out.println("Enter task description:");
                String description = sc.nextLine();
                createTask(title, description);
                break;

            case "list":
                listTasks();
                break;

            case "complete":
                if (args.length < 2) {
                    System.err.println("USAGE: provide task ID to complete.");
                    return;
                }
                int taskId = Integer.parseInt(args[1]);
                completeTask(taskId);
                break;
            case "delete":
                if(args.length < 2){
                    System.err.println("USAGE: provied task ID to complete.");
                    return;
                }
                taskId = Integer.parseInt(args[1]);
                deleteTask(taskId);
                break;
            default:
                System.err.println("Invalid command. Use 'add' to add a task, 'list' to list tasks, 'complete <task_id>' to mark a task as completed or 'delete <task_id> to delete a task");
                break;
        }
    }
}



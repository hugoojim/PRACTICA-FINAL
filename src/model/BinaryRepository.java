package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BinaryRepository implements IRepository {
    private final String filePath;

    // Constructor que recibe la ruta del archivo binario
    public BinaryRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Task addTask(Task task) throws RepositoryException {
        ArrayList<Task> tasks = getAllTask();
        tasks.removeIf(t -> t.getIdentifier() == task.getIdentifier()); // Evitar duplicados
        tasks.add(task);

        try {
            saveTasks(tasks); // Guardar las tareas
        } catch (Exception e) {
            throw new RepositoryException("Error al guardar los cambios en el archivo: " + e.getMessage(), e);
        }
        return task;
    }

    @Override
    public void removeTask(Task task) throws RepositoryException {
        ArrayList<Task> tasks = getAllTask();
        boolean removed = tasks.removeIf(t -> t.getIdentifier() == task.getIdentifier());
        if (!removed) {
            throw new RepositoryException("No se encontró la tarea con ID: " + task.getIdentifier());
        }

        try {
            saveTasks(tasks);
        } catch (Exception e) {
            throw new RepositoryException("Error al guardar los cambios en el archivo: " + e.getMessage(), e);
        }
    }

    @Override
    public void modifyTask(Task task) throws RepositoryException {
        ArrayList<Task> tasks = getAllTask();
        tasks.removeIf(t -> t.getIdentifier() == task.getIdentifier());
        tasks.add(task);

        try {
            saveTasks(tasks);
        } catch (Exception e) {
            throw new RepositoryException("Error al guardar los cambios en el archivo: " + e.getMessage(), e);
        }
    }

    @Override
    public ArrayList<Task> getAllTask() throws RepositoryException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Task> tasks = (ArrayList<Task>) ois.readObject();
            for (Task task : tasks) {
                if (task.getDate() == null) {
                    task.setDate(new Date());
                }
            }
            return tasks;
        } catch (IOException | ClassNotFoundException e) {
            throw new RepositoryException("Error al leer las tareas desde el archivo", e);
        }
    }

    @Override
    public void saveTasks(ArrayList<Task> tasks) throws RepositoryException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            throw new RepositoryException("Error al guardar las tareas en el archivo: " + e.getMessage(), e);
        }
    }

    @Override
    public void createTask(Task task) {
        try {
            addTask(task);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Task> getAllTasks() {
        try {
            return getAllTask();
        } catch (RepositoryException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void updateTaskByIdentifier(Task task) {
        try {
            modifyTask(task);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTaskByIdentifier(Task task) {
        try {
            removeTask(task);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}

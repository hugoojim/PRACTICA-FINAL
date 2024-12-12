package model;

import java.util.ArrayList;
import java.util.List;

public interface IRepository {
    Task addTask(Task task) throws RepositoryException;

    void removeTask(Task task) throws RepositoryException;

    void modifyTask(Task task) throws RepositoryException;

    ArrayList<Task> getAllTask() throws RepositoryException;

    void saveTasks(ArrayList<Task> tasks) throws RepositoryException;
    void createTask(Task task);
    List<Task> getAllTasks();
    void updateTaskByIdentifier(Task task);
    void deleteTaskByIdentifier(Task task);
    
}

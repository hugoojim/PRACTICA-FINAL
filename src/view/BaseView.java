package view;

import model.Task;
import java.util.List;
import controller.Controller;

public abstract class BaseView {
    protected Controller controller;

    // Constructor
    public BaseView(Controller controller) {
        this.controller = controller;
    }

    // Métodos abstractos existentes
    public abstract void init();
    public abstract void showMessage(String message);
    public abstract void showErrorMessage(String errorMessage);
    public abstract void end();

    // Nuevo método abstracto para mostrar una lista de tareas
    public abstract void showTaskList(List<Task> tasks);

    // Nuevo método abstracto para mostrar detalles de una tarea
    public abstract void showTaskDetails(Task task);
    public abstract void showCrudMenu();
}

package controller;
import model.Model;
import model.Task;
import view.BaseView;

import java.util.List;

public class Controller {
    private final Model model;
    private BaseView view;

    // Constructor
    public Controller(Model model, BaseView view) {
        this.model = model;
        this.view = view;
    }

    // Setter para la vista
    public void setView(BaseView view) {
        this.view = view;
    }

    // Inicia la aplicación
    public void startApplication() {
        try {
            model.loadData(); // Carga datos iniciales
            view.showMessage("Datos cargados correctamente.");
            view.init(); // Inicia la vista
        } catch (Exception e) {
            view.showErrorMessage("Error al cargar los datos: " + e.getMessage());
        }
    }
    

    // Finaliza la aplicación
    public void exitApplication() {
        try {
            model.saveData(); // Guarda los datos
            view.showMessage("Aplicación cerrada y datos guardados correctamente.");
        } catch (Exception e) {
            view.showErrorMessage("Error al guardar los datos: " + e.getMessage());
        }
    }

    public void handleAction(String action, Object... args) {
        try {
            switch (action.toLowerCase()) {
                // CRUD Operations
                case "addtask" -> handleAddTask((Task) args[0]);
                case "listuncompleted" -> handleListUncompletedTasks();
                case "listall" -> handleListAllTasks();
                case "detailtask" -> handleTaskDetails((int) args[0]);
                case "togglecompleted" -> toggleTaskCompleted((int) args[0]);
case "edittask" -> editTask((int) args[0], (String) args[1], (String) args[2], (int) args[3], (int) args[4]);
case "removetask" -> handleRemoveTask((int) args[0]);

    
                // Export/Import Operations
                case "exporttocsv" -> handleExportToCSV();
                case "exporttojson" -> handleExportToJSON();
                case "importfromcsv" -> handleImportFromCSV();
                case "importfromjson" -> handleImportFromJSON();
    
                // Default case for unrecognized actions
                default -> view.showErrorMessage("Acción no reconocida.");
            }
        } catch (Exception e) {
            view.showErrorMessage("Error al ejecutar la acción: " + e.getMessage());
        }
    }
    private void toggleTaskCompleted(int id) throws Exception {
        // Obtener la tarea por ID
        Task task = model.getTaskById(id);
        if (task != null) {
            // Cambiar el estado de completado
            task.setCompleted(!task.isCompleted());
            // Guardar los cambios en el modelo
            model.modifyTask(task);
            // Mostrar mensaje de éxito
            view.showMessage("Estado de la tarea actualizado correctamente.");
        } else {
            // Mostrar mensaje de error si la tarea no existe
            view.showErrorMessage("Tarea no encontrada.");
        }
    }
    private void editTask(int id, String title, String content, int priority, int estimatedDuration) throws Exception {
        // Obtener la tarea por ID
        Task task = model.getTaskById(id);
        if (task != null) {
            // Actualizar los atributos de la tarea
            task.setTitle(title);
            task.setContent(content);
            task.setPriority(priority);
            task.setEstimatedDuration(estimatedDuration);
    
            // Guardar los cambios en el modelo
            model.modifyTask(task);
    
            // Mostrar mensaje de éxito
            view.showMessage("Tarea modificada correctamente.");
        } else {
            // Mostrar mensaje de error si la tarea no existe
            view.showErrorMessage("Tarea no encontrada.");
        }
    }
    
    
    private void handleAddTask(Task task) throws Exception {
        model.addTask(task);
        view.showMessage("Tarea añadida correctamente.");
    }

    public boolean isTaskCompleted(int id) throws Exception {
    Task task = model.getTaskById(id); // Obtener la tarea por ID
    if (task != null) {
        return task.isCompleted(); // Devuelve el estado completado/pendiente
    } else {
        throw new Exception("Tarea no encontrada."); // Lanza una excepción si no existe
    }
}

    
   
   
    
    private void handleTaskDetails(int id) throws Exception {
        Task task = model.getTaskById(id);
        if (task != null) {
            view.showTaskDetails(task);
        } else {
            view.showErrorMessage("Tarea no encontrada.");
        }
    }
    
    private void handleRemoveTask(int id) throws Exception {
        Task task = model.getTaskById(id);
        if (task != null) {
            model.removeTask(task);
            view.showMessage("Tarea eliminada correctamente.");
        } else {
            view.showErrorMessage("Tarea no encontrada.");
        }
    }
    
    
    

    private void handleListUncompletedTasks() throws Exception {
        List<Task> tasks = model.getUncompletedTasksByPriority(); // Llama al modelo
        System.out.println("Tareas enviadas a la vista: " + tasks); // Depuración
        view.showTaskList(tasks); // Envía a la vista
    }
    
    

    // Maneja la acción de listar todas las tareas
    private void handleListAllTasks() throws Exception {
        List<Task> tasks = model.getAllTasksOrderedByDate();
        view.showTaskList(tasks);
    }

    
    private void handleExportToCSV() throws Exception {
        String filePath = System.getProperty("user.home") + "/tasks.csv"; // Ruta del archivo CSV
        model.exportToCSV(filePath); // Llamar al método de exportación con la ruta
        view.showMessage("Tareas exportadas a CSV correctamente en " + filePath);
    }
    
    private void handleExportToJSON() throws Exception {
        String filePath = System.getProperty("user.home") + "/tasks.json"; // Ruta del archivo JSON
        model.exportToJSON(filePath); // Llamar al método de exportación con la ruta
        view.showMessage("Tareas exportadas a JSON correctamente en " + filePath);
    }
    
    
    private void handleImportFromCSV() {
        try {
            model.importFromCSV(System.getProperty("user.home") + "/tasks.csv");
            view.showMessage("Tareas importadas desde CSV correctamente.");
        } catch (Exception e) {
            view.showErrorMessage("Error al importar las tareas: " + e.getMessage());
        }
    }
    
    private void handleImportFromJSON() throws Exception {
        String filePath = System.getProperty("user.home") + "/tasks.json"; // Ruta del archivo JSON
        model.importFromJSON(filePath); // Llamar al método de importación con la ruta
        view.showMessage("Tareas importadas desde JSON correctamente desde " + filePath);
    }
    
    }
    


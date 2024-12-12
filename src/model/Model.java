package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Model {
    public void setExporter(IExporter exporter){
        this.exporter = exporter;
    }
    private final IRepository repository; // Repositorio para gestionar las tareas
    private IExporter exporter; // Exportador dinámico para CSV o JSON

    // Constructor que recibe un repositorio
    public Model(IRepository repository) {
        this.repository = repository;
    }


    // CRUD - Operaciones básicas

    // Añadir una tarea
    public Task addTask(Task task) throws RepositoryException {
        return repository.addTask(task); // Añade la tarea al repositorio
    }

    // Eliminar una tarea
    public void removeTask(Task task) throws RepositoryException {
        repository.removeTask(task);
    }
    

    // Modificar una tarea
    public void modifyTask(Task task) throws RepositoryException {
        repository.modifyTask(task);
    }
    
    // Obtener todas las tareas
    public ArrayList<Task> getAllTasks() throws RepositoryException {
        return repository.getAllTask(); // Devuelve todas las tareas del repositorio
    }

    // Listados específicos

    // Tareas sin completar, ordenadas por prioridad (mayor a menor)
    public List<Task> getUncompletedTasksOrderedByPriority() throws RepositoryException {
        return repository.getAllTask()
                .stream()
                .filter(task -> !task.isCompleted()) // Filtra tareas sin completar
                .sorted((t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority())) // Ordena por prioridad descendente
                .collect(Collectors.toList());
    }
     // Carga los datos desde el repositorio
     public void loadData() throws Exception {
        // Verifica si el repositorio tiene tareas, si no devuelve una lista vacía
        List<Task> tasks = repository.getAllTask();
        // No necesitas realizar ninguna acción adicional si ya usas tareas directamente desde el repositorio
    }

    // Guarda los datos en el repositorio
    public void saveData() throws Exception {
        // Obtén todas las tareas y guárdalas en el repositorio
        repository.saveTasks(repository.getAllTask());
    }


    // Historial completo de tareas, ordenadas por fecha (más recientes primero)
    public List<Task> getAllTasksOrderedByDate() throws RepositoryException {
        return repository.getAllTask()
                .stream()
                .sorted((t1, t2) -> {
                    if (t1.getDate() == null && t2.getDate() == null) return 0;
                    if (t1.getDate() == null) return 1; // Tareas con fecha null al final
                    if (t2.getDate() == null) return -1;
                    return t2.getDate().compareTo(t1.getDate()); // Ordenar por fecha descendente
                })
                .collect(Collectors.toList());
    }
    
    

    // Alternar estado de una tarea (completada/pendiente)
    public void toggleTaskCompletion(int taskId) throws RepositoryException {
        ArrayList<Task> tasks = repository.getAllTask(); // Obtener todas las tareas
        for (Task task : tasks) {
            if (task.getIdentifier() == taskId) {
                task.setCompleted(!task.isCompleted()); // Alternar estado
                repository.modifyTask(task); // Guardar cambios en el repositorio
                System.out.println("Tarea actualizada: " + (task.isCompleted() ? "Completada" : "Pendiente"));
                return;
            }
        }
        throw new RepositoryException("Tarea no encontrada con ID: " + taskId);
    }

    // Exportación e Importación usando IExporter

    // Exportar tareas utilizando el exportador actual
    public void exportTasks(String filePath) throws ExporterException, RepositoryException {
        if (exporter == null) {
            throw new ExporterException("No se ha establecido un exportador.");
        }
        exporter.export(filePath, repository.getAllTask()); // Usa el exportador para exportar
    }

    // Obtener tareas completadas
    public List<Task> getCompletedTasks() throws RepositoryException {
    return repository.getAllTask()
            .stream()
            .filter(Task::isCompleted) // Filtrar tareas completadas
            .collect(Collectors.toList());
}


    // Importar tareas utilizando el exportador actual
    public void importTasks(String filePath) throws ExporterException, RepositoryException {
        if (exporter == null) {
            throw new ExporterException("No se ha establecido un exportador.");
        }
        List<Task> importedTasks = exporter.importTasks(filePath);
        ArrayList<Task> tasks = repository.getAllTask();

        for (Task importedTask : importedTasks) {
            if (tasks.stream().noneMatch(t -> t.getIdentifier() == importedTask.getIdentifier())) {
                repository.addTask(importedTask);
            }
        }
    }

    // Método auxiliar para imprimir todas las tareas actuales
    public void printAllTasks() throws RepositoryException {
        System.out.println("\nEstado actual del repositorio:");
        for (Task task : repository.getAllTask()) {
            System.out.println("- " + task.getTitle() + " (Completada: " + task.isCompleted() + ")");
        }
    }
    public IRepository getRepository() {
        return repository;
    }

    public Task getTaskById(int id) throws RepositoryException {
        return repository.getAllTask().stream()
                .filter(task -> task.getIdentifier() == id)
                .findFirst()
                .orElse(null);
    }
    
public List<Task> getUncompletedTasksByPriority() throws RepositoryException {
    return repository.getAllTask().stream()
        .filter(task -> !task.isCompleted()) // Filtrar tareas sin completar
        .sorted(Comparator.comparingInt(Task::getPriority)) // Ordenar por prioridad ascendente
        .collect(Collectors.toList()); // Convertir a lista
}




public void exportToCSV(String filePath) throws Exception {
    this.exporter = ExporterFactory.getExporter("csv");
    exporter.export(filePath, repository.getAllTask());
}

public void exportToJSON(String filePath) throws Exception {
    this.exporter = ExporterFactory.getExporter("json");
    exporter.export(filePath, repository.getAllTask());
}

public void importFromCSV(String filePath) throws Exception {
    this.exporter = ExporterFactory.getExporter("csv");
    List<Task> tasks = exporter.importTasks(filePath);
    for (Task task : tasks) {
        repository.addTask(task); // Agrega las tareas al repositorio
    }
}


public void importFromJSON(String filePath) throws Exception {
    this.exporter = ExporterFactory.getExporter("json");
    List<Task> tasks = exporter.importTasks(filePath);
    for (Task task : tasks) {
        repository.addTask(task);
    }
}



}

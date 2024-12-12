package view;

import controller.Controller;
import model.Task;
import java.util.Date;
import java.util.Scanner;
import java.util.List;

public class ConsoleView extends BaseView {
    private final Scanner scanner = new Scanner(System.in);

    // Constructor
    public ConsoleView(Controller controller) {
        super(controller);
    }
    
    @Override
    public void init() {
        showMessage("Bienvenido a la aplicación de gestión de tareas.");
        boolean running = true;

        while (running) {
            showMainMenu();
            int choice = readInt("Elige una opción: ");
            switch (choice) {
                case 1 -> handleCrudMenu();
                case 2 -> handleExportImportMenu();
                case 3 -> {
                    end();
                    running = false;
                }
                default -> showErrorMessage("Opción no válida.");
            }
        }
    }
    

    private void showMainMenu() {
        System.out.println("\nMenú Principal:");
        System.out.println("1. Menú CRUD");
        System.out.println("2. Menú Exportación/Importación");
        System.out.println("3. Salir");
    }

    private void handleCrudMenu() {
        boolean running = true;
        while (running) {
            showCrudMenu();
            int choice = readInt("Elige una opción: ");
            switch (choice) {
                case 1 -> { // Alta de tarea
                    int id = readInt("Introduce el ID de la tarea: ");
                    String title = readString("Introduce el título de la tarea: ");
                    String content = readString("Introduce el contenido: ");
                    int priority = readInt("Introduce la prioridad (1-5): ");
                    int estimatedDuration = readInt("Introduce la duración estimada (en minutos): ");
                    Task task = new Task(id, title, new Date(), content, priority, estimatedDuration, false);
                    controller.handleAction("addtask", task);
                }
                case 2 -> controller.handleAction("listuncompleted"); // Listado de tareas sin completar
                case 3 -> controller.handleAction("listall"); // Listado de todas las tareas
                case 4 -> { // Detalle de una tarea
                    int id = readInt("Introduce el ID de la tarea para ver detalles: ");
                    handleTaskDetails(id);
                }
                case 5 -> running = false; // Volver al menú principal
                default -> showErrorMessage("Opción no válida.");
            }
        }
    }
    private void handleTaskDetails(int id) {
        try {
            controller.handleAction("detailtask", id); // Muestra los detalles de la tarea
            boolean taskRunning = true;
    
            while (taskRunning) {
                System.out.println("1. Marcar como " + 
                    (controller.isTaskCompleted(id) ? "pendiente" : "completada"));
                System.out.println("2. Modificar tarea");
                System.out.println("3. Eliminar tarea");
                System.out.println("4. Volver al menú principal");
                int taskChoice = readInt("Elige una opción: ");
    
                switch (taskChoice) {
                    case 1 -> controller.handleAction("togglecompleted", id); // Alternar estado
                    case 2 -> handleEditTask(id); // Modificar tarea
                    case 3 -> {
                        controller.handleAction("removetask", id); // Eliminar tarea
                        taskRunning = false; // Salir después de eliminar
                    }
                    case 4 -> taskRunning = false; // Volver al menú CRUD
                    default -> showErrorMessage("Opción no válida.");
                }
            }
        } catch (Exception e) {
            showErrorMessage("Error al obtener los detalles de la tarea: " + e.getMessage());
        }
    }
    

    private void handleEditTask(int id) {
        String title = readString("Introduce el nuevo título: ");
        String content = readString("Introduce el nuevo contenido: ");
        int priority = readInt("Introduce la nueva prioridad (1-5): ");
        int estimatedDuration = readInt("Introduce la nueva duración estimada (en minutos): ");
        try {
            controller.handleAction("edittask", id, title, content, priority, estimatedDuration);
            showMessage("Tarea modificada correctamente.");
        } catch (Exception e) {
            showErrorMessage("Error al modificar la tarea: " + e.getMessage());
        }
    }
    

    
  
    private void handleExportImportMenu() {
        boolean running = true;
        while (running) {
            showExportImportMenu();
            int choice = readInt("Elige una opción: ");
            switch (choice) {
                case 1 -> controller.handleAction("exporttocsv");
                case 2 -> controller.handleAction("exporttojson");
                case 3 -> controller.handleAction("importfromcsv");
                case 4 -> controller.handleAction("importfromjson");
                case 5 -> running = false;
                default -> showErrorMessage("Opción no válida.");
            }
        }
    }
    
    private void showExportImportMenu() {
        System.out.println("Menú Exportación/Importación:");
        System.out.println("1. Exportar a CSV");
        System.out.println("2. Exportar a JSON");
        System.out.println("3. Importar desde CSV");
        System.out.println("4. Importar desde JSON");
        System.out.println("5. Volver al menú principal");
    }
    

    @Override
    public void showMessage(String message) {
        System.out.println("Mensaje: " + message);
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        System.err.println("Error: " + errorMessage);
    }

    @Override
    public void end() {
        showMessage("Gracias por usar la aplicación. Saliendo...");
        controller.exitApplication();
    }


    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.err.print("Por favor, introduce un número válido: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        return value;
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    @Override
    public void showTaskList(List<Task> tasks) {
        System.out.println("Prioridades de las tareas (orden ascendente):");
        for (Task task : tasks) {
            System.out.println(task.getPriority());
        }
    
        System.out.println("Listado de tareas:");
        for (Task task : tasks) {
            System.out.printf("ID: %d, Título: %s, Prioridad: %d, Duración: %d minutos, Estado: %s%n",
                    task.getIdentifier(),
                    task.getTitle(),
                    task.getPriority(),
                    task.getEstimatedDuration(),
                    task.isCompleted() ? "Completada" : "Pendiente");
            System.out.println("-------------------");
        }
    }

    
    
    @Override
    public void showTaskDetails(Task task) {
        System.out.println("\nDetalles de la tarea:");
        System.out.println("ID: " + task.getIdentifier());
        System.out.println("Título: " + task.getTitle());
        System.out.println("Contenido: " + task.getContent());
        System.out.println("Prioridad: " + task.getPriority());
        System.out.println("Duración estimada: " + task.getEstimatedDuration() + " minutos");
        System.out.println("Estado: " + (task.isCompleted() ? "Completada" : "Pendiente"));
    }
    @Override
    public void showCrudMenu() {
        System.out.println("Menú CRUD:");
        System.out.println("1. Alta de tarea");
        System.out.println("2. Listado de tareas sin completar (ordenadas por prioridad)");
        System.out.println("3. Listado completo de tareas");
        System.out.println("4. Detalle de tarea con opciones avanzadas (marcar, modificar, eliminar)");
        System.out.println("5. Volver al menú principal");
    }
    

}

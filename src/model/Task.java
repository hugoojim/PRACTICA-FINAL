package model;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador único para la serialización

    private int identifier;
    private String title;
    private Date date;
    private String content;
    private int priority;
    private int estimatedDuration;
    private boolean completed;
    
      // Constructor sin parámetros
      public Task() {
    }
    // Constructor
    public Task(int identifier, String title, Date date, String content, int priority, int estimatedDuration, boolean completed) {
        this.identifier = identifier;
        this.title = title;
        this.date = (date != null) ? date : new Date(); // Si `date` es null, asigna la fecha actual
        this.content = (content == null || content.trim().isEmpty()) ? "No especificado" : content; // Manejo de contenido vacío
        this.priority = priority;
        this.estimatedDuration = estimatedDuration;
        this.completed = completed;

        System.out.println("Nueva tarea creada: " + this.title + " con prioridad " + this.priority);
    }
    public void setDate(Date date) {
        this.date = date;
    }
    
 
    // Getters y Setters
    public int getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = (content == null || content.trim().isEmpty()) ? "No especificado" : content;
    }

    public int getPriority() {
        return priority;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
public String toString() {
    return "ID: " + identifier + ", Título: " + title + ", Prioridad: " + priority +
           ", Duración: " + estimatedDuration + " minutos, Estado: " + (completed ? "Completada" : "Pendiente");
}
public String toCSV() {
    return identifier + "," + title + "," + date + "," + content + "," + priority + "," + estimatedDuration + "," + completed;
}

public static Task fromCSV(String csv) {
    String[] parts = csv.split(",");
    return new Task(
        Integer.parseInt(parts[0]), // ID
        parts[1],                  // Título
        new Date(parts[2]),        // Fecha
        parts[3],                  // Contenido
        Integer.parseInt(parts[4]), // Prioridad
        Integer.parseInt(parts[5]), // Duración
        Boolean.parseBoolean(parts[6]) // Completado
    );
}
public void setTitle(String title) {
    this.title = title;
}


public void setPriority(int priority) {
    this.priority = priority;
}

public void setEstimatedDuration(int estimatedDuration) {
    this.estimatedDuration = estimatedDuration;
}
public void setIdentifier(int identifier) {
    this.identifier = identifier;
}



}

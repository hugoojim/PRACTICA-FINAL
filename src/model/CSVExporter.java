package model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.ParseException;



public class CSVExporter implements IExporter {
    @Override
public void export(String filePath, List<Task> tasks) throws ExporterException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
        for (Task task : tasks) {
            writer.printf("%d,%s,%s,%s,%d,%d,%b%n",
                    task.getIdentifier(),
                    task.getTitle(),
                    (task.getDate() != null ? task.getDate().toString() : ""), // Maneja `null`
                    task.getContent(),
                    task.getPriority(),
                    task.getEstimatedDuration(),
                    task.isCompleted());
        }
    } catch (IOException e) {
        throw new ExporterException("Error al exportar las tareas", e);
    }
}


@Override
public List<Task> importTasks(String filePath) throws ExporterException {
    List<Task> tasks = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.ENGLISH);
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("Leyendo línea: " + line); // Depuración
            String[] fields = line.split(",");
            if (fields.length < 7) {
                System.err.println("Formato inválido: " + line); // Depuración
                throw new ExporterException("El archivo CSV tiene un formato inválido.");
            }
            // Analiza la fecha o asigna la fecha actual si el campo está vacío
            Date date = fields[2].isEmpty() ? new Date() : dateFormat.parse(fields[2]);
            tasks.add(new Task(
                    Integer.parseInt(fields[0]),  // ID
                    fields[1],                   // Título
                    date,                        // Fecha
                    fields[3],                   // Contenido
                    Integer.parseInt(fields[4]), // Prioridad
                    Integer.parseInt(fields[5]), // Duración estimada
                    Boolean.parseBoolean(fields[6]) // Estado completado
            ));
        }
    } catch (IOException | ParseException e) {
        System.err.println("Error durante la importación: " + e.getMessage()); // Depuración
        throw new ExporterException("Error al importar las tareas", e);
    }
    return tasks;
}




}

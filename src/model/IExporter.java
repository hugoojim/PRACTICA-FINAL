package model;

import java.util.List;

public interface IExporter {
    void export(String filePath, List<Task> tasks) throws ExporterException;

    List<Task> importTasks(String filePath) throws ExporterException; // Método actualizado
}

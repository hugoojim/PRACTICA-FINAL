package model;

import com.google.gson.Gson;
import java.io.*;
import java.util.List;

public class JSONExporter implements IExporter {
    @Override
    public void export(String filePath, List<Task> tasks) throws ExporterException {
        try (Writer writer = new FileWriter(filePath)) {
            Gson gson = new Gson();
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            throw new ExporterException("Error al exportar a JSON", e);
        }
    }

    @Override
public List<Task> importTasks(String filePath) throws ExporterException {
    try (Reader reader = new FileReader(filePath)) {
        Gson gson = new Gson();
        Task[] tasksArray = gson.fromJson(reader, Task[].class);
        return List.of(tasksArray);
    } catch (IOException e) {
        throw new ExporterException("Error al importar desde JSON", e);
    }
}

}

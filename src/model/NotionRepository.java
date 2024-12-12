package model;

import notion.api.v1.NotionClient;
import notion.api.v1.http.OkHttp5Client;
import notion.api.v1.logging.Slf4jLogger;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageParent;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.model.pages.PageProperty.RichText;
import notion.api.v1.model.pages.PageProperty.RichText.Text;
import notion.api.v1.request.databases.QueryDatabaseRequest;
import notion.api.v1.request.pages.CreatePageRequest;
import notion.api.v1.request.pages.UpdatePageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotionRepository implements IRepository {

    private final NotionClient client;
    private final String databaseId;

    public NotionRepository(String apiToken, String databaseId) {
        this.client = new NotionClient(apiToken);
        this.client.setHttpClient(new OkHttp5Client(60000, 60000, 60000));
        this.client.setLogger(new Slf4jLogger());
        this.databaseId = databaseId;
    }

    @Override
    public Task addTask(Task task) throws RepositoryException {
        try {
            Map<String, PageProperty> properties = Map.of(
                "Identifier", createTitleProperty(String.valueOf(task.getIdentifier())),
                "Title", createRichTextProperty(task.getTitle()),
                "Content", createRichTextProperty(task.getContent()),
                "Priority", createNumberProperty(task.getPriority()),
                "EstimatedDuration", createNumberProperty(task.getEstimatedDuration()),
                "Completed", createCheckboxProperty(task.isCompleted())
            );

            PageParent parent = PageParent.database(databaseId);
            CreatePageRequest request = new CreatePageRequest(parent, properties);
            Page response = client.createPage(request);

            System.out.println("Tarea creada con ID: " + response.getId());
            return task;
        } catch (Exception e) {
            throw new RepositoryException("Error al agregar la tarea", e);
        }
    }

    @Override
    public void removeTask(Task task) throws RepositoryException {
        try {
            String pageId = findPageIdByIdentifier(String.valueOf(task.getIdentifier()));
            if (pageId == null) {
                throw new RepositoryException("Tarea no encontrada.");
            }

            UpdatePageRequest updateRequest = new UpdatePageRequest(pageId, Collections.emptyMap(), true);
            client.updatePage(updateRequest);

            System.out.println("Tarea eliminada.");
        } catch (Exception e) {
            throw new RepositoryException("Error al eliminar la tarea", e);
        }
    }

    @Override
    public void modifyTask(Task task) throws RepositoryException {
        try {
            String pageId = findPageIdByIdentifier(String.valueOf(task.getIdentifier()));
            if (pageId == null) {
                throw new RepositoryException("Tarea no encontrada.");
            }

            Map<String, PageProperty> updatedProperties = Map.of(
                "Title", createRichTextProperty(task.getTitle()),
                "Content", createRichTextProperty(task.getContent()),
                "Priority", createNumberProperty(task.getPriority()),
                "EstimatedDuration", createNumberProperty(task.getEstimatedDuration()),
                "Completed", createCheckboxProperty(task.isCompleted())
            );

            UpdatePageRequest updateRequest = new UpdatePageRequest(pageId, updatedProperties);
            client.updatePage(updateRequest);

            System.out.println("Tarea actualizada.");
        } catch (Exception e) {
            throw new RepositoryException("Error al modificar la tarea", e);
        }
    }

    @Override
    public ArrayList<Task> getAllTask() throws RepositoryException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            QueryDatabaseRequest queryRequest = new QueryDatabaseRequest(databaseId);
            QueryResults queryResults = client.queryDatabase(queryRequest);

            for (Page page : queryResults.getResults()) {
                Map<String, PageProperty> properties = page.getProperties();
                Task task = mapPageToTask(page.getId(), properties);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            throw new RepositoryException("Error al obtener las tareas", e);
        }
        return tasks;
    }

    @Override
    public void saveTasks(ArrayList<Task> tasks) throws RepositoryException {
        for (Task task : tasks) {
            addTask(task);
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

    // Métodos auxiliares
    private String findPageIdByIdentifier(String identifier) {
        try {
            QueryDatabaseRequest queryRequest = new QueryDatabaseRequest(databaseId);
            QueryResults queryResults = client.queryDatabase(queryRequest);

            for (Page page : queryResults.getResults()) {
                Map<String, PageProperty> properties = page.getProperties();
                if (properties.containsKey("Identifier") &&
                        properties.get("Identifier").getTitle().get(0).getText().getContent().equals(identifier)) {
                    return page.getId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Task mapPageToTask(String pageId, Map<String, PageProperty> properties) {
        try {
            Task task = new Task();
            task.setIdentifier(Integer.parseInt(properties.get("Identifier").getTitle().get(0).getText().getContent()));
            task.setTitle(properties.get("Title").getRichText().get(0).getText().getContent());
            task.setContent(properties.get("Content").getRichText().get(0).getText().getContent());
            task.setPriority(properties.get("Priority").getNumber().intValue());
            task.setEstimatedDuration(properties.get("EstimatedDuration").getNumber().intValue());
            task.setCompleted(properties.get("Completed").getCheckbox());
            return task;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PageProperty createTitleProperty(String title) {
        RichText idText = new RichText();
        idText.setText(new Text(title));
        PageProperty idProperty = new PageProperty();
        idProperty.setTitle(Collections.singletonList(idText));
        return idProperty;
    }

    private PageProperty createRichTextProperty(String text) {
        RichText richText = new RichText();
        richText.setText(new Text(text));
        PageProperty property = new PageProperty();
        property.setRichText(Collections.singletonList(richText));
        return property;
    }

    private PageProperty createNumberProperty(Integer number) {
        PageProperty property = new PageProperty();
        property.setNumber(number);
        return property;
    }

    private PageProperty createCheckboxProperty(boolean checked) {
        PageProperty property = new PageProperty();
        property.setCheckbox(checked);
        return property;
    }
}

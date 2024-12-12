import controller.Controller;
import model.*;
import view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        try {
            // Crear repositorio con base en los argumentos de línea de comandos
            IRepository repository;
            String filePath = "./tasks.bin";  // Ruta por defecto para el repositorio binario

            // Verificar si se pasó el parámetro --repository
            if (args.length > 1 && args[0].equals("--repository")) {
                String repoType = args[1].toLowerCase();

                // Si el repositorio es binario
                if (repoType.equals("bin")) {
                    repository = new BinaryRepository(filePath);
                } 
                // Si el repositorio es Notion
                else if (repoType.equals("notion") && args.length > 3) {
                    String apiKey = args[2];  // API_KEY
                    String databaseId = args[3];  // DATABASE_ID
                    repository = new NotionRepository(apiKey, databaseId);
                } else {
                    throw new IllegalArgumentException("Tipo de repositorio no válido. Solo se admite 'bin' o 'notion'.");
                }
            } else {
                // Valor por defecto: bin
                repository = new BinaryRepository(filePath);
            }

            // Configuración del modelo, controlador y vista
            Model model = new Model(repository);
            Controller controller = new Controller(model, null);
            ConsoleView view = new ConsoleView(controller);

            // Asignar la vista al controlador e iniciar la aplicación
            controller.setView(view);
            controller.startApplication();
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
        }
    }
}


package model;

public class ExporterFactory {
    public static IExporter getExporter(String type) throws ExporterException {
        return switch (type.toLowerCase()) {
            case "csv" -> new CSVExporter();
            case "json" -> new JSONExporter();
            default -> throw new ExporterException("Tipo de exportador no reconocido: " + type);
        };
    }
}

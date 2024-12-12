package model;

public class ExporterException extends Exception {
    public ExporterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExporterException(String message) {
        super(message);
    }
}

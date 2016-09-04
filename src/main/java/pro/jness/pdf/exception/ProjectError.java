package pro.jness.pdf.exception;

/**
 * @author Aleksandr Streltsov
 */
public class ProjectError extends RuntimeException {

    public ProjectError(String message) {
        super(message);
    }

    public ProjectError(String message, Throwable cause) {
        super(message, cause);
    }
}

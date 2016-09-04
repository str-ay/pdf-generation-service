package pro.jness.pdf.exception;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
public class PdfCreationException extends Exception {

    public PdfCreationException(String message) {
        super(message);
    }

    public PdfCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

package pro.jness.pdf.utils;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
public class PdfGenerationResult {
    private PdfCreationStatus status;
    private String message;

    public PdfGenerationResult(PdfCreationStatus status) {
        this.status = status;
    }

    public PdfGenerationResult(PdfCreationStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public PdfCreationStatus getStatus() {
        return status;
    }

    public void setStatus(PdfCreationStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

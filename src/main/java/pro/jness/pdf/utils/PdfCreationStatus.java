package pro.jness.pdf.utils;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
public enum PdfCreationStatus {
    DONE(true),
    QUEUED(false),
    FAILED(true);

    private boolean finalStatus;

    PdfCreationStatus(boolean finalStatus) {
        this.finalStatus = finalStatus;
    }

    public boolean isFinalStatus() {
        return finalStatus;
    }
}

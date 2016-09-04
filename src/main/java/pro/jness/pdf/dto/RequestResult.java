package pro.jness.pdf.dto;

import pro.jness.pdf.utils.PdfCreationStatus;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
public class RequestResult {
    private String taskId;
    private PdfCreationStatus status;
    private String description;

    public RequestResult() {
    }

    public RequestResult(String taskId, PdfCreationStatus status, String description) {
        this.taskId = taskId;
        this.status = status;
        this.description = description;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public PdfCreationStatus getStatus() {
        return status;
    }

    public void setStatus(PdfCreationStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

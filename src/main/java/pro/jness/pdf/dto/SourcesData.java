package pro.jness.pdf.dto;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
public class SourcesData {
    private byte[] template;
    private byte[] data;
    private String taskId;

    public SourcesData(byte[] template, byte[] data, String taskId) {
        this.template = template;
        this.data = data;
        this.taskId = taskId;
    }

    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}

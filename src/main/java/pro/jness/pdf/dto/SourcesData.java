package pro.jness.pdf.dto;

import java.io.File;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
public class SourcesData {
    private File template;
    private File data;
    private String taskId;

    public SourcesData(File template, File data, String taskId) {
        this.template = template;
        this.data = data;
        this.taskId = taskId;
    }

    public File getTemplate() {
        return template;
    }

    public void setTemplate(File template) {
        this.template = template;
    }

    public File getData() {
        return data;
    }

    public void setData(File data) {
        this.data = data;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}

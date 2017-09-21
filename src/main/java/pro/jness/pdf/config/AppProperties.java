package pro.jness.pdf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 15/12/2016
 */
@Component
@ConfigurationProperties(prefix = "pdfgs")
public class AppProperties {

    @NotNull
    private String version;
    private String tasksDirectory;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTasksDirectory() {
        return tasksDirectory;
    }

    public void setTasksDirectory(String tasksDirectory) {
        this.tasksDirectory = tasksDirectory;
    }
}

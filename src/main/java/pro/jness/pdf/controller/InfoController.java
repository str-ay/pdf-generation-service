package pro.jness.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.jness.pdf.config.AppProperties;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 24/08/16
 */
@RestController
@RequestMapping(value = "/info")
public class InfoController {

    private final AppProperties appProperties;

    @Autowired
    public InfoController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/version")
    public VersionInfo version() {
        return new VersionInfo(appProperties.getVersion(), appProperties.getBuildInfo());
    }

    public static class VersionInfo {
        private String version;
        private String buildInfo;

        public VersionInfo() {
        }

        VersionInfo(String version, String buildInfo) {
            this.version = version;
            this.buildInfo = buildInfo;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getBuildInfo() {
            return buildInfo;
        }

        public void setBuildInfo(String buildInfo) {
            this.buildInfo = buildInfo;
        }
    }
}

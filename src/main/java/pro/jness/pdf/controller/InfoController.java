package pro.jness.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping
    public String version() {
        return appProperties.getVersion();
    }
}

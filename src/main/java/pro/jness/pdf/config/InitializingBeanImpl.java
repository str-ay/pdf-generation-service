package pro.jness.pdf.config;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import pro.jness.pdf.config.AppProperties;
import pro.jness.pdf.exception.PdfCreationException;
import pro.jness.pdf.utils.ClassNameUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
public class InitializingBeanImpl implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ClassNameUtil.getCurrentClassName());

    private final AppProperties appProperties;

    @Autowired
    public InitializingBeanImpl(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public void afterPropertiesSet() throws Exception {
        pdfWarmingUp();
        cleaningUp();
    }

    private void cleaningUp() throws IOException, PdfCreationException {
        logger.info("Cleaning up...");
        File tasks = new File(appProperties.getTasksDirectory());
        makeDirectory();
        FileUtils.cleanDirectory(tasks);
        logger.info("Cleaning up complete");
    }

    private File makeDirectory() throws PdfCreationException {
        File file = new File(appProperties.getTasksDirectory());
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new PdfCreationException("Can not create directory " + file.getAbsolutePath());
            }
        }
        return file;
    }

    private void pdfWarmingUp() {
        logger.info("Warming up...");
        try {
            File file = File.createTempFile("warming_up" + UUID.randomUUID().toString().replaceAll("-", ""), ".pdf");
            try (FileOutputStream out = new FileOutputStream(file)) {
                ClassPathResource template = new ClassPathResource("warming_up.odt");
                IXDocReport report = XDocReportRegistry.getRegistry().
                        loadReport(template.getInputStream(), TemplateEngineKind.Freemarker);
                Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.ODFDOM);
                IContext ctx = report.createContext();
                ctx.put("var", "value");
                report.convert(ctx, options, out);
            }
        } catch (IOException | XDocReportException e) {
            logger.error("Warming up failed." + e.getMessage(), e);
        }
        logger.info("Warming up complete");
    }
}
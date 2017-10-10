package pro.jness.pdf.config.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.jness.pdf.service.PdfCreationService;
import pro.jness.pdf.utils.ClassNameUtil;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 * on 27/08/16
 */
@Component
public class QueueListener {
    private static final Logger logger = LoggerFactory.getLogger(ClassNameUtil.getCurrentClassName());

    private final PdfCreationService pdfCreationService;

    @Autowired
    public QueueListener(PdfCreationService pdfCreationService) {
        this.pdfCreationService = pdfCreationService;
    }

    @Scheduled(fixedRate = 1000 * 60 * 2)
    public void configureTasks() {
        logger.info(pdfCreationService.getInfo());
    }
}

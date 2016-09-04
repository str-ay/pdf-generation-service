package pro.jness.pdf.service.impl;

import org.springframework.stereotype.Service;
import pro.jness.pdf.utils.PdfGenerationTask;
import pro.jness.pdf.dto.SourcesData;
import pro.jness.pdf.service.PdfCreationService;
import pro.jness.pdf.utils.PdfQueueService;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
@Service
public class PdfCreationServiceImpl implements PdfCreationService {

    @Override
    public void newTask(SourcesData sourcesData) {
        PdfQueueService.submit(new PdfGenerationTask(sourcesData));
    }
}

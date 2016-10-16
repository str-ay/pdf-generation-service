package pro.jness.pdf.service;

import pro.jness.pdf.dto.SourcesData;
import pro.jness.pdf.exception.PdfCreationException;
import pro.jness.pdf.utils.PdfGenerationResult;
import pro.jness.pdf.utils.PdfGenerationTask;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
public interface PdfCreationService {
    void submit(PdfGenerationTask task);

    boolean isDone(String taskId);

    boolean isCanceled(String taskId);

    PdfGenerationResult getStatus(String taskId) throws PdfCreationException;

    String getInfo();

    void newTask(SourcesData sourcesData);
}

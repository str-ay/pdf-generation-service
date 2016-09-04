package pro.jness.pdf.service;

import pro.jness.pdf.dto.SourcesData;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
public interface PdfCreationService {

    void newTask(SourcesData sourcesData);
}

package pro.jness.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.jness.pdf.utils.PdfCreationStatus;
import pro.jness.pdf.dto.RequestResult;
import pro.jness.pdf.dto.SourcesData;
import pro.jness.pdf.config.AppProperties;
import pro.jness.pdf.exception.PdfCreationException;
import pro.jness.pdf.service.PdfCreationService;
import pro.jness.pdf.utils.PdfGenerationResult;
import pro.jness.pdf.utils.PdfQueueService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 25/08/16
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    private final AppProperties appProperties;
    private final PdfCreationService pdfCreationService;

    @Autowired
    public TaskController(PdfCreationService pdfCreationService, AppProperties appProperties) {
        this.pdfCreationService = pdfCreationService;
        this.appProperties = appProperties;
    }

    @PostMapping("/make")
    @ResponseBody
    public RequestResult make(@RequestParam("data") MultipartFile data,
                              @RequestParam("template") MultipartFile template) throws Exception {
        String token = generateToken();
        SourcesData sourcesData = saveSources(token, template, data);
        pdfCreationService.newTask(sourcesData);
        return new RequestResult(token, PdfCreationStatus.QUEUED, "");
    }

    @RequestMapping(value = "/{id}/check", method = RequestMethod.GET)
    @ResponseBody
    public RequestResult check(@PathVariable("id") String taskId) throws PdfCreationException {
        PdfGenerationResult results = PdfQueueService.getStatus(taskId);
        return new RequestResult(taskId, results.getStatus(), results.getMessage());
    }

    @RequestMapping(value = "/{id}/result", method = RequestMethod.GET)
    @ResponseBody
    public void getResult(@PathVariable("id") String taskId, HttpServletResponse response) throws PdfCreationException, IOException {
        File file = new File(new File(appProperties.getTasksDirectory(), taskId), taskId + ".pdf");
        if (!PdfQueueService.isDone(taskId) || !file.exists()) {
            String errorMessage;
            if (!PdfQueueService.isDone(taskId)) {
                errorMessage = "Task is not completed";
            } else {
                errorMessage = "Task is completed but result does not exists.";
            }
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return;
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", String.format("inline; filename=\"%s\"", file.getName()));
        response.setContentLength((int) file.length());
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
    }

    private SourcesData saveSources(String taskId, MultipartFile template, MultipartFile data) throws Exception {
        File dir = makeTaskDirectoryIfNotExists(taskId);
        SourcesData sourcesData = new SourcesData(new File(dir, "template.odt"), new File(dir, "data.json"), taskId);
        template.transferTo(sourcesData.getTemplate());
        data.transferTo(sourcesData.getData());
        return sourcesData;
    }

    private File makeTaskDirectoryIfNotExists(String taskId) throws PdfCreationException {
        File file = new File(appProperties.getTasksDirectory(), taskId);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new PdfCreationException("Can not create directory " + file.getAbsolutePath());
            }
        }
        return file;
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}

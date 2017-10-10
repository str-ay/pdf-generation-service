package pro.jness.pdf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.jness.pdf.utils.ClassNameUtil;
import pro.jness.pdf.utils.PdfCreationStatus;
import pro.jness.pdf.dto.RequestResult;
import pro.jness.pdf.dto.SourcesData;
import pro.jness.pdf.config.AppProperties;
import pro.jness.pdf.exception.PdfCreationException;
import pro.jness.pdf.service.PdfCreationService;
import pro.jness.pdf.utils.PdfGenerationResult;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 * on 25/08/16
 */
@Controller
@RequestMapping("/task")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(ClassNameUtil.getCurrentClassName());
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
        SourcesData sourcesData = new SourcesData(template.getBytes(), data.getBytes(), token);
        pdfCreationService.newTask(sourcesData);
        return new RequestResult(token, PdfCreationStatus.QUEUED, "");
    }

    @RequestMapping(value = "/{id}/check", method = RequestMethod.GET)
    @ResponseBody
    public RequestResult check(@PathVariable("id") String taskId) throws PdfCreationException {
        PdfGenerationResult results = pdfCreationService.getStatus(taskId);
        return new RequestResult(taskId, results.getStatus(), results.getMessage());
    }

    @RequestMapping(value = "/{id}/result", method = RequestMethod.GET)
    @ResponseBody
    public void getResult(@PathVariable("id") String taskId, HttpServletResponse response) throws PdfCreationException, IOException {
        Path result = Paths.get(appProperties.getTasksDirectory()).resolve(taskId + ".pdf");
        String errorMessage = null;
        if (!pdfCreationService.isDone(taskId)) {
            errorMessage = "Task is not completed";
        }

        if (!Files.exists(result)) {
            errorMessage = "Task is completed but result does not exists.";
        }

        if (errorMessage != null) {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", String.format("inline; filename=\"%s\"", result.getFileName()));
        response.setContentLength((int) Files.size(result));
        try (InputStream inputStream = Files.newInputStream(result)) {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }

        Files.delete(result);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}

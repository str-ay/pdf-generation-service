package pro.jness.pdf.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.jness.pdf.dto.SourcesData;
import pro.jness.pdf.exception.PdfCreationException;
import pro.jness.pdf.exception.TaskNotFoundException;
import pro.jness.pdf.utils.*;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 26/08/16
 */
@Service
public class PdfCreationServiceImpl implements PdfCreationService {

    private static final Logger logger = LoggerFactory.getLogger(ClassNameUtil.getCurrentClassName());

    private static ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private static ConcurrentHashMap<String, Future<PdfGenerationResult>> taskResults = new ConcurrentHashMap<>();

    @Override
    public void submit(PdfGenerationTask task) {
        taskResults.put(task.getSourcesData().getTaskId(), executorService.submit(task));
    }

    @Override
    public boolean isDone(String taskId) {
        if (taskResults.get(taskId) == null) {
            throw new TaskNotFoundException("Task [" + taskId + "] not found.");
        }
        return taskResults.get(taskId).isDone();
    }

    @Override
    public boolean isCanceled(String taskId) {
        return isDone(taskId) && taskResults.get(taskId).isCancelled();
    }

    @Override
    public PdfGenerationResult getStatus(String taskId) throws PdfCreationException {
        if (isDone(taskId)) {
            if (isCanceled(taskId)) {
                return new PdfGenerationResult(PdfCreationStatus.FAILED, "Unexpected error");
            } else {
                try {
                    return taskResults.get(taskId).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new PdfCreationException("Can not get results");
                }
            }
        }
        return new PdfGenerationResult(PdfCreationStatus.QUEUED);
    }

    @Override
    public String getInfo() {
        return String.format("PoolSize: %d, Active: %d, Queue: %d",
                executorService.getPoolSize(), executorService.getActiveCount(), executorService.getQueue().size());
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                logger.error("Pool did not terminated in 5 seconds!");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void newTask(SourcesData sourcesData) {
        submit(new PdfGenerationTask(sourcesData));
    }
}

package pro.jness.pdf.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.jness.pdf.exception.PdfCreationException;
import pro.jness.pdf.exception.TaskNotFoundException;

import java.util.concurrent.*;

/**
 * @author Aleksandr Streltsov
 *         2016.04.18
 */
public class PdfQueueService {

    private static final Logger logger = LoggerFactory.getLogger(ClassNameUtil.getCurrentClassName());

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private static ConcurrentHashMap<String, Future<PdfGenerationResult>> taskResults = new ConcurrentHashMap<>();

    public static void submit(PdfGenerationTask task) {
        taskResults.put(task.getSourcesData().getTaskId(), executor.submit(task));
    }

    public static boolean isDone(String taskId) {
        if (taskResults.get(taskId) == null) {
            throw new TaskNotFoundException("Task [" + taskId + "] not found.");
        }
        return taskResults.get(taskId).isDone();
    }

    public static boolean isCanceled(String taskId) {
        return isDone(taskId) && taskResults.get(taskId).isCancelled();
    }

    public static PdfGenerationResult getStatus(String taskId) throws PdfCreationException {
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

    public static String getInfo() {
        return String.format("PoolSize: %d, Active: %d, Queue: %d",
                executor.getPoolSize(), executor.getActiveCount(), executor.getQueue().size());
    }
}

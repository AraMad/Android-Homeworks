package ua.arina.task4.dataloaders;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * Created by Arina on 19.03.2017
 */

public class DownloadComplete extends ExecutorCompletionService{
    private ExecutorService executorService;

    public DownloadComplete(ExecutorService executor) {
        super(executor);
        executorService = executor;
    }

    public void shutDown(){
        executorService.shutdown();
    }

    public boolean isTerminated(){
        return executorService.isTerminated();
    }

    public void shutdownNow(){
        executorService.shutdownNow();
    }
}

package pl.matbartc.librarian.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.service.DocumentStatusReporter;
import pl.matbartc.librarian.service.DownloadProcessor;
import pl.matbartc.librarian.service.DownloadExecutor;
import pl.matbartc.librarian.storage.DocumentStorage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class RunnableDownloadProcessor extends DownloadProcessor implements Runnable {

    final Logger log = LoggerFactory.getLogger(RunnableDownloadProcessor.class);
    final ConcurrentLinkedQueue<Document> taskQueue;

    public RunnableDownloadProcessor(
            DocumentStorage storage,
            DocumentStatusReporter documentStatusReporter,
            DownloadExecutor downloadExecutor,
            ConcurrentLinkedQueue<Document> taskQueue
    ) {
        super(storage, documentStatusReporter, downloadExecutor);
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Document document = taskQueue.poll();

                if (document != null) {
                    process(document);
                } else {
                    Thread.sleep(100);
                }
            }

            log.info("DownloadExecutor shutdown...");
        } catch (InterruptedException e) {
            log.error("Executor interrupted", e);
        }
    }
}

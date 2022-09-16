package pl.matbartc.librarian.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.service.DocumentStatusReporter;
import pl.matbartc.librarian.service.impl.executors.DownloadExecutor;
import pl.matbartc.librarian.service.impl.executors.JdkHttpDownloadExecutor;
import pl.matbartc.librarian.storage.DocumentStorage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DownloadWorker implements Runnable {

    final Logger log = LoggerFactory.getLogger(DownloadWorker.class);

    final DocumentStorage storage;
    final DocumentStatusReporter documentStatusReporter;
    final DownloadExecutor downloadExecutor;
    final ConcurrentLinkedQueue<Document> taskQueue;

    public DownloadWorker(
            DocumentStorage storage,
            DocumentStatusReporter documentStatusReporter,
            ConcurrentLinkedQueue<Document> taskQueue) {
        this.storage = storage;
        this.documentStatusReporter = documentStatusReporter;
        this.taskQueue = taskQueue;
        this.downloadExecutor = new JdkHttpDownloadExecutor(storage, documentStatusReporter);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Document document = taskQueue.poll();

                if (document != null) {
                    try {
                        downloadExecutor.download(document);
                    } catch (Exception e) {
                        documentStatusReporter.updateStatus(document.getId(), DocumentStatus.ERROR);
                        log.error("Download executor failed with", e);
                    }
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

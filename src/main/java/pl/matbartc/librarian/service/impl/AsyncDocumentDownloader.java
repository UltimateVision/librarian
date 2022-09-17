package pl.matbartc.librarian.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.service.DocumentDownloader;
import pl.matbartc.librarian.service.DocumentStatusReporter;
import pl.matbartc.librarian.service.DownloadExecutor;
import pl.matbartc.librarian.storage.DocumentStorage;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AsyncDocumentDownloader implements DocumentDownloader {

    private final ConcurrentLinkedQueue<Document> documentQueue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executorService;

    private DocumentStorage storage;

    private DocumentStatusReporter documentStatusReporter;

    private DownloadExecutor downloadExecutor;

    @Autowired
    public AsyncDocumentDownloader(
            @Value("${librarian.downloaderPoolSize:1}") int executorPoolSize,
            DocumentStorage storage,
            DocumentStatusReporter documentStatusReporter,
            DownloadExecutor downloadExecutor
    ) {
        executorService = Executors.newFixedThreadPool(executorPoolSize);
        this.storage = storage;
        this.documentStatusReporter = documentStatusReporter;
        this.downloadExecutor = downloadExecutor;
    }

    @PostConstruct
    public void init() {
        executorService.submit(new RunnableDownloadProcessor(storage, documentStatusReporter, downloadExecutor, documentQueue));
    }

    @Override
    public void scheduleDownload(Document document) {
        documentQueue.add(document);
    }
}

package pl.matbartc.librarian.service.impl.executors;

import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.service.DocumentStatusReporter;
import pl.matbartc.librarian.storage.DocumentStorage;

public abstract class DownloadExecutor {

    final DocumentStorage storage;
    final DocumentStatusReporter statusReporter;

    protected DownloadExecutor(DocumentStorage storage, DocumentStatusReporter statusReporter) {
        this.storage = storage;
        this.statusReporter = statusReporter;
    }

    public abstract void download(Document document) throws Exception;
}

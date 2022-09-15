package pl.matbartc.librarian.service;

import static org.asynchttpclient.Dsl.*;

import org.asynchttpclient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.matbartc.librarian.model.Document;
import pl.matbartc.librarian.model.DocumentStatus;
import pl.matbartc.librarian.repo.DocumentsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class DocumentService {

    final AsyncHttpClient asyncHttpClient = asyncHttpClient();

    @Autowired
    DocumentsRepository repository;

    public Document scheduleDownload(String source) {
        Document document = new Document();
        document.setSource(source);

        document = repository.save(document);

        final UUID id = document.getId();

        asyncHttpClient.prepareGet(source).execute(new AsyncCompletionHandler<Void>() {
            @Override
            public Void onCompleted(Response response) throws Exception {
                final Document d = repository.findById(id).orElseThrow(); // FIXME
                d.setData(response.getResponseBodyAsBytes());
                d.setContentType(response.getContentType());
                d.setStatus(DocumentStatus.READY);
                repository.save(d);

                return null;
            }
        });

        return document;
    }

    public Optional<DocumentStatus> getDocumentStatus(UUID uuid) {
        return repository.findById(uuid).map(Document::getStatus);
    }

    public Optional<Document> getDocument(UUID uuid) {
        return repository.findById(uuid);
    }
}

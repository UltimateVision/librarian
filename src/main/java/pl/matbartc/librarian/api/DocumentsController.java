package pl.matbartc.librarian.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.matbartc.librarian.model.Document;
import pl.matbartc.librarian.model.dto.DocumentDTO;
import pl.matbartc.librarian.model.dto.DownloadDocumentDTO;
import pl.matbartc.librarian.repo.DocumentsRepository;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentsController {

    Logger log = LoggerFactory.getLogger(DocumentsController.class);

    @Autowired
    DocumentsRepository documentsRepository;

    @GetMapping
    public String hello() {
        return "Hello world!";
    }

    @PostMapping
    public DocumentDTO scheduleDownload(@RequestBody DownloadDocumentDTO downloadDocumentDTO) {
        Document document = new Document();

        document.setSource(downloadDocumentDTO.getUrl());
        document = documentsRepository.save(document);

        DocumentDTO response = new DocumentDTO();

        response.setId(document.getId().toString());
        response.setStatus(document.getStatus());

        return response;
    }

    @GetMapping("/info/{id}")
    public Document getDocumentInfo(@PathVariable String id) {
        final UUID uuid = UUID.fromString(id);

        log.info("UUID: {}", id);

        return documentsRepository.findById(uuid).orElse(null); // FIXME
    }

    @GetMapping("/info")
    public Iterable<Document> getDocumentsInfo() {
        return documentsRepository.findAll();
    }
}

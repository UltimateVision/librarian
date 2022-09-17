package pl.matbartc.librarian.service;

import pl.matbartc.librarian.model.entities.Document;

public interface DocumentDownloader {

    void scheduleDownload(Document document);
}

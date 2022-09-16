package pl.matbartc.librarian.service;

import pl.matbartc.librarian.model.entities.Document;

public interface DocumentDownloader {

    void schedule(Document document);
}

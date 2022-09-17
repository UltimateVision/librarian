package pl.matbartc.librarian.service;

import pl.matbartc.librarian.model.DocumentData;
import pl.matbartc.librarian.model.entities.Document;

public interface DownloadExecutor {

    DocumentData download(Document document) throws Exception;
}

package pl.matbartc.librarian.exceptions;

public class DownloadFailureException extends RuntimeException {

    public DownloadFailureException(String message) {
        super(message);
    }
}

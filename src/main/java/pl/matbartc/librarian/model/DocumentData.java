package pl.matbartc.librarian.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DocumentData {
    private final byte[] content;
    private final String contentType;
}

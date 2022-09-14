package pl.matbartc.librarian.repo;

import org.springframework.data.repository.CrudRepository;
import pl.matbartc.librarian.model.Document;

import java.util.UUID;

public interface DocumentsRepository extends CrudRepository<Document, UUID> {
}

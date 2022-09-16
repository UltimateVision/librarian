package pl.matbartc.librarian.storage;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.matbartc.librarian.model.entities.Document;
import pl.matbartc.librarian.model.DocumentStatus;

import java.util.UUID;

public interface DocumentsRepository extends CrudRepository<Document, UUID> {

    @Transactional
    @Modifying
    @Query("update Document d set d.status = :status where d.id = :id")
    int updateDocumentStatus(@Param("id") UUID id, @Param("status") DocumentStatus status);

    Document findBySource(String source);
}

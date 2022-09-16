package pl.matbartc.librarian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("pl.matbartc.librarian.storage")
@EntityScan("pl.matbartc.librarian.model.entities")
public class LibrarianApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibrarianApplication.class, args);
	}

}

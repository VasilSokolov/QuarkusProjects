package repository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import model.Film;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FilmRepositoryTest {

    @Inject
    FilmRepository filmRepository;
    @Test
    void getFilm() {
        Optional<Film> film = filmRepository.getFilm(5);

        assertTrue(film.isPresent());
        assertEquals("AFRICAN EGG", film.get().getTitle());
    }

//    @Test
//    void getFilms() {
//    }
//
//    @Test
//    void paged() {
//    }
//
//    @Test
//    void actors() {
//    }
//
//    @Test
//    void updateRentalRate() {
//    }
}
package controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.Film;
import repository.FilmRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/film")
public class FilmController {

    @Inject
    FilmRepository filmRepository;

    @GET
    @Path("/hello-world")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(){
        return "Hello";
    }

    @GET
    @Path("/{filmId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFilmById(Integer filmId){
        Optional<Film> film = filmRepository.getFilm(filmId);
        return film.isPresent() ? film.get().getTitle() : "No film was found";
    }

    @GET
    @Path("/pagedFilms/{page}/{minLength}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFilmById(long page, Integer minLength){
        return filmRepository.paged(page, minLength)
                .map(f -> String.format("%s (%d min)", f.getTitle(), f.getLength()))
                .collect(Collectors.joining("\n"));
    }

    @GET
    @Path("/actors/{startWith}/{minLength}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFilmById(String startWith, Integer minLength){
        return filmRepository.actors(startWith, minLength)
                .map(f -> String.format("%s (%d min): %s",
                        f.getTitle(),
                        f.getLength(),
                        f.getActors().stream()
                                .map(a -> String.format("%s %s",
                                        a.getFirstName(),
                                        a.getLastUpdate()))
                                .collect(Collectors.joining(", "))))
                .collect(Collectors.joining("\n"));
    }

    @GET
    @Path("/updateByRentalRate/{rentalRate}/{minLength}")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateFilmByRentalRate(BigDecimal rentalRate, Integer minLength){
        filmRepository.updateRentalRate(minLength, rentalRate);

        return filmRepository.getFilmsByMinLength(minLength)
                .map(f -> String.format("%s (%d min) - $%f", f.getTitle(), f.getLength(), f.getRentalRate()))
                .collect(Collectors.joining("\n"));
    }
}

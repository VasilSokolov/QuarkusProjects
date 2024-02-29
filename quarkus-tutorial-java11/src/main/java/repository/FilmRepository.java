package repository;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.speedment.jpastreamer.projection.Projection;
import com.speedment.jpastreamer.streamconfiguration.StreamConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.Film;
import model.Film$;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

@ApplicationScoped
public class FilmRepository {

    private static final int PAGE_SIZE = 20;

    @Inject
    JPAStreamer jpaStreamer;

    public Optional<Film> getFilm(Integer filmId){
        return jpaStreamer.stream(Film.class)
                .filter(Film$.id.equal(filmId))
                .findFirst();
    }

    public Stream<Film> getFilms(Integer minLength){
        return jpaStreamer.stream(Film.class)
                .filter(Film$.length.greaterThan(minLength))
                .sorted(Film$.length);
    }

    public Stream<Film> paged(long page, int minLength) {
        return jpaStreamer.stream(Projection.select(
                        Film$.id,
                        Film$.title,
                        Film$.length))
                .filter(Film$.length.greaterThan(minLength))
                .sorted(Film$.length)
                .skip(page * PAGE_SIZE)
                .limit(PAGE_SIZE);
    }

//    public Stream<Film> paged(long page, int minLength) {
//        return jpaStreamer.stream(Film.class)
//                .filter(Film$.length.greaterThan(minLength))
//                .sorted(Film$.length)
//                .skip(page * PAGE_SIZE)
//                .limit(PAGE_SIZE);
//    }

    public Stream<Film> actors(String startsWith, int minLength) {
        final StreamConfiguration<Film> sc = StreamConfiguration.of(Film.class)
                .joining(Film$.actors);
        return jpaStreamer.stream(sc)
                .filter(Film$.title.startsWith(startsWith).and(Film$.length.greaterThan(minLength)))
                .sorted(Film$.length.reversed());
    }

    @Transactional
    public void updateRentalRate(int minLength, BigDecimal rentalRate){
        jpaStreamer.stream(Film.class)
                .filter(Film$.length.greaterThan(minLength))
                .forEach(f -> {
                    f.setRentalRate(rentalRate);
                });
    }
}

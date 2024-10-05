package repository;

import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import model.City;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CityRepository {

    private static final int PAGE_SIZE = 20;

    @Inject
    JPAStreamer jpaStreamer;

    public List<City> getAllCities(){
        return jpaStreamer.stream(City.class)
                .collect(Collectors.toList());
    }
}

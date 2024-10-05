package controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.City;
import repository.CityRepository;

import java.util.List;
import java.util.stream.Collectors;

@Path("/city")
public class CityController {

    @Inject
    CityRepository cityRepository;

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCities(){
        List<City> cities = cityRepository.getAllCities();
//        System.out.println(cities);
        return !cities.isEmpty()
                ?  cities.stream()
                .map(f-> String.format("City:%s", f.getCity()))
                .collect(Collectors.joining("\n"))
                : "No city was found";
    }
}

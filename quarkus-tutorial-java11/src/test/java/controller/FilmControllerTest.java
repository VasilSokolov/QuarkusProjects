package controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;


@QuarkusTest
class FilmControllerTest {

    @Test
    public void hello() {
        given()
                .when().get("/film/5")
                .then()
                .statusCode(200)
                .body(containsString("AFRICAN EGG"));
    }

//    @Test
//    void getFilmById() {
//    }
//
//    @Test
//    void testGetFilmById() {
//    }
//
//    @Test
//    void testGetFilmById1() {
//    }
//
//    @Test
//    void updateFilmByRentalRate() {
//    }
}
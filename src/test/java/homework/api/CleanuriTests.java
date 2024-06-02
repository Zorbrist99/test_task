package homework.api;

import homework.api.config.UrlConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static homework.api.specs.Spec.requestSpecification;
import static homework.api.specs.Spec.responseSpecification;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class CleanuriTests {
    UrlConfig config = ConfigFactory.create(UrlConfig.class, System.getProperties());

    @BeforeAll
    public static void beforeAll() {
        RestAssured.baseURI = "https://cleanuri.com/api/v1";
    }


    @Test
    @Tag("Positive")
    @DisplayName("Сократить корректный URL")
    void shortenCorrectUrl() {
        given(requestSpecification)
                .formParam("url", config.getCorrectUrl())

                .when()
                .post("/shorten")

                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("result_url", notNullValue());
    }

    @Test
    @Tag("Positive")
    @DisplayName("Сократить длинный  URL")
    void shortenLongUrl() {
        given(requestSpecification)
                .formParam("url", config.getLongUrl())


                .when()
                .post("/shorten")

                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("result_url", notNullValue());
    }

    @Test
    @Tag("Positive")
    @DisplayName("Сократить URL и перейти по нему. [BUG] 404 Короткая ссылка не работает")
    void shortenUrlClickOnIt() {
        Response response = given(requestSpecification)
                .formParam("url", config.getCorrectUrl())

                .when()
                .post("/shorten")

                .then()
                .spec(responseSpecification)
                .body("result_url", notNullValue())
                .statusCode(200)
                .extract().response();

        String newUrl = response.jsonPath().getString("result_url");

        given()
                .redirects().follow(false)
                .head(newUrl)
                .then()
                .statusCode(301);


//        Response newResponse = given()
//                .log().body()
//                .when()
//                .get(newUrl)
//                .then()
//                .log().body()
//                .extract().response();

//        Response redirectResponse = given()
//                .redirects().follow(false)
//                .get(newUrl);
//
//        assertEquals(301, redirectResponse.getStatusCode());
//        assertEquals(correctUrl, redirectResponse.getHeader("Location"));
    }

    @Test
    @Tag("Negative")
    @DisplayName("Сократить URL без протокола")
    void shorteningIncorrectUrl() {
        given(requestSpecification)
                .formParam("url", config.getIncorrectUrl())

                .when()
                .post("/shorten")

                .then()
                .spec(responseSpecification)
                .statusCode(400);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Сократить пустой URL")
    void shorteningEmptyUrl() {
        given(requestSpecification)
                .formParam("url", config.getEmptyUrl())

                .when()
                .post("/shorten")

                .then()
                .spec(responseSpecification)
                .statusCode(400);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Сократить URL с пробелами")
    void shorteningUrlWithSpaces() {
        given(requestSpecification)
                .formParam("url", config.getUrlWithOmissions())

                .when()
                .post("/shorten")

                .then()
                .spec(responseSpecification)
                .statusCode(400);
    }
}

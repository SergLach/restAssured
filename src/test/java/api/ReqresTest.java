package api;

import com.google.gson.Gson;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;


class ReqresTest {

    private static final String URL = "https://reqres.in/";
    public static final String API_USERS_2 = "api/users/2";
    Logger LOG = LoggerFactory.getLogger(ReqresTest.class);


    @Description("Check avatar and Id test")
    @Test
    @Story("Regression")
    @Story("Smoke")
    void checkAvatarAndIdTest() {
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + "api/users?page=2")
                .then().log().all()
                .statusCode(200)
                .extract().body().jsonPath().getList("data", UserData.class);
        users.forEach(x -> Assertions.assertTrue(x.getAvatar().contains(x.getId().toString() + "-image.jpg")));
        Assertions.assertTrue(users.stream().allMatch(end -> end.getEmail().endsWith("@reqres.in")));
        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> id = users.stream().map(avatar -> avatar.getId().toString()).collect(Collectors.toList());
        for (int i = 0; i < avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(id.get(i)));
        }
    }

    @Test
    void checkPostmanMockTest() {
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get("https://349d2eb7-48e0-4e5c-9ee1-99a7602ca219.mock.pstmn.io")
                .then().log().all()
                .statusCode(200)
                .extract().body().jsonPath().getList("data", UserData.class);
        String name = users.stream().map(UserData::getFirst_name).collect(Collectors.joining());
        Integer id = users.stream().map(UserData::getId).findFirst().orElse(0);
        Assertions.assertEquals("Serg", name);
        LOG.info("User name with id - {} is {}. ", id, name);
    }

    @Test
    void createUserWithPostmanMockTest() {
        UserTime time = new UserTime("Serg", "tester");
        RegisteredUser response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(time)
                .post("https://349d2eb7-48e0-4e5c-9ee1-99a7602ca219.mock.pstmn.io")
                .then().log().all()
                .statusCode(201)
                .extract().as(RegisteredUser.class);

        String regex = ".{0,11}$";

        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        LOG.info("Current system time - {}", currentTime);
        LOG.info("Response time - {}", response.getCreatedAt().replaceAll(".{0,6}$", ""));
        Assertions.assertTrue(currentTime.contains(response.getCreatedAt().replaceAll(".{0,6}$", "")));

    }

    @Test
    @Story("Smoke")
    @Story("Regression")
    void checkAvatarAndIdUsingSpecificationTest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(200));

        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        users.forEach(x -> Assertions.assertTrue(x.getAvatar().contains(x.getId().toString())));
        Assertions.assertTrue(users.stream().allMatch(end -> end.getEmail().endsWith("@reqres.in")));
        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> id = users.stream().map(avatar -> avatar.getId().toString()).collect(Collectors.toList());
        for (int i = 0; i < avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(id.get(i)));
        }
    }

    @Test
    @Story("Regression")
    void successRegistrationTest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(200));

        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register register = new Register("eve.holt@reqres.in", "pistol");
        SuccessRegistration successRegistration = given()
                .when()
                .body(register)
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessRegistration.class);

        Assertions.assertNotNull(successRegistration.getId());
        Assertions.assertNotNull(successRegistration.getToken());
        Assertions.assertEquals(id, successRegistration.getId());
        Assertions.assertEquals(token, successRegistration.getToken());
    }

    @Test
    @Story("Regression")
    @Story("Run_first")
    void unSuccessRegTest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(400));
        Register register = new Register("eve.holt@reqres.in", "");
        UnSuccessRed unSuccessRed = given()
                .body(register)
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessRed.class);
        Assertions.assertEquals(unSuccessRed.getError(), "Missing password");
    }

    @Test
    @Story("Regression")
    void sortedYearsTest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(200));
        List<ColorsData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);

        List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
        Assertions.assertEquals(sortedYears, years);
    }

    @Test
    @Story("Regression")
    void deleteUsertest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(204));
        given()
                .when()
                .delete(API_USERS_2)
                .then().log().all();
    }

    @Test
    @Story("Regression")
    void timeTest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(200));

        UserTime time = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .when()
                .body(time)
                .put(API_USERS_2)
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        String regex = "^(.*).{5}$";

        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        System.out.println(currentTime);
        Assertions.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
        System.out.println(response.getUpdatedAt().replaceAll(regex, ""));
    }

    @Test
    void patchUserDataTest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(200));
        UserTime time = new UserTime("morpheusPatch", "zion resident patch");
        UserTimeResponse response = given()
                .when()
                .body(time)
                .patch(API_USERS_2)
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        Assertions.assertEquals("morpheusPatch", response.getName());
        Assertions.assertEquals("zion resident patch", response.getJob());
    }

    @Test
    void singleUserDoNotFoundTest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(404));
        given()
                .when()
                .get("api/users/23")
                .then().log().all();
    }
}

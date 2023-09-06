package api;

import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;


public class ReqresTest {

    private static final String URL = "https://reqres.in/";

    @Test
    @Story("Regression")
    @Story("Smoke")
    public void checkAvatarAndIdTest() {
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + "api/users?page=2")
                .then().log().all()
                .statusCode(200)
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
    @Story("Smoke")
    @Story("Regression")
    public void checkAvatarAndIdUsingSpecificationTest() {
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
    public void successRegistrationTest() {
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
    public void unSuccessRegTest() {
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
    public void sortedYearsTest() {
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
    public void deleteUsertest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    @Story("Regression")
    public void timeTest() {
        Specification.installSpecification(Specification.requestSpecification(URL), Specification.resSpec(200));

        UserTime time = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .when()
                .body(time)
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        String regex = "^(.*).{5}$";

        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");
        System.out.println(currentTime);
        Assertions.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex, ""));
        System.out.println(response.getUpdatedAt().replaceAll(regex, ""));
    }
}

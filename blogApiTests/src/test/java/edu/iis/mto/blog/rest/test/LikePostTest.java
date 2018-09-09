package edu.iis.mto.blog.rest.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;

public class LikePostTest extends FunctionalTests {

    public static final String CONFIRMED_USER_NOT_OWNER = "/blog/user/3/like/1";
    public static final String CONFIRMED_USER_AND_OWNER = "/blog/user/1/like/1";
    public static final String NEW_USER_NOT_OWNER = "/blog/user/2/like/1";

    private final JSONObject like = new JSONObject();

    @Test
    public void confirmedUserAllowedToLikeOtherUserPost() {
        RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                .body(like.toString())
                .expect().log().all().statusCode(HttpStatus.SC_OK).when().post(CONFIRMED_USER_NOT_OWNER);
    }

    @Test
    public void newUserShouldNotBeAllowedToLikePost() {
        RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                .body(like.toString())
                .expect().log().all().statusCode(HttpStatus.SC_BAD_REQUEST).when().post(NEW_USER_NOT_OWNER);
    }

    @Test
    public void anyUserShouldNotBeAllowedToLikeOwnPost() {
        RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                .body(like.toString())
                .expect().log().all().statusCode(HttpStatus.SC_BAD_REQUEST).when().post(CONFIRMED_USER_AND_OWNER);
    }

    @Test
    public void createLikeTwiceShouldNotChangeLikesCount() {
        for(int i = 0; i < 2; i++) {
            RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                    .body(like.toString())
                    .expect().log().all().statusCode(HttpStatus.SC_OK).when().post(CONFIRMED_USER_NOT_OWNER);
        }

        String urlGetFirstPost = "/blog/user/1/post";
        RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                .body(like.toString()).expect().log().all().body(containsString("\"likesCount\":1")).when()
                .get(urlGetFirstPost);
    }
}

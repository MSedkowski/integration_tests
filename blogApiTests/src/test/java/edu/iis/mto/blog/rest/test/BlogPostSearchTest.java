package edu.iis.mto.blog.rest.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;

public class BlogPostSearchTest extends FunctionalTests {

    private static final String EXISTING_USER_POSTS = "/blog/user/1/post";
    private static final String REMOVED_USER_POSTS = "/blog/user/4/post";
    private static final String CONFIRMED_USER_NOT_OWNER = "/blog/user/3/like/1";

    private static final JSONObject user = new JSONObject();

    @Test
    public void searchingExistingUserPostShouldReturnCorrectNumberOfLikes() {
        getUserPosts("\"likesCount\":0", EXISTING_USER_POSTS);

        RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                .body(user.toString()).expect().log().all().statusCode(HttpStatus.SC_OK).when()
                .post(CONFIRMED_USER_NOT_OWNER);

        getUserPosts("\"likesCount\":1", EXISTING_USER_POSTS);
    }

    @Test
    public void serachingRemovedUserPostsShouldReturnBadRequest() {
        RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                .body(user.toString())
                .expect().log().all().statusCode(HttpStatus.SC_BAD_REQUEST).when().get(REMOVED_USER_POSTS);
    }

    private void getUserPosts(String likesCount, String path) {
        RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                .body(user.toString())
                .expect().log().all().body(containsString(likesCount)).when().get(path);
    }
}

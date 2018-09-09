package edu.iis.mto.blog.rest.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;

public class SearchUserTest extends FunctionalTests {

    public final static String FIND_USER = "/blog/user/find";

    public static final JSONObject user = new JSONObject();

    @Test
    public void searchingUsersShouldNotReturnRemovedOnes() {
        RestAssured.given().accept(ContentType.JSON).header("Content-Type", "application/json;charset=UTF-8")
                .body(user.toString()).param("searchString", "@").expect().log().all()
                .body(not((containsString("\"firstName\":\"Kelly\"")))).when().get(FIND_USER);
    }
}

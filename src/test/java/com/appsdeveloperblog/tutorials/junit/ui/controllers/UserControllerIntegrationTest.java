package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

//通常のDIコンテナと基本的に同じ挙動のDIコンテナを生成
//E2Eテストやインテグレーションテストで使用する
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//並行テストでポート番号が競合しないように、ランダムなポート番号を使用する
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
//        properties = {"server.port=8081", "hostname=192.168.0.2"})
//テストメソッド毎に異なるプロパティファイルを指定できる
//@TestPropertySource(locations = "/application-test.properties")
//@TestPropertySource(locations = "/application-test.properties", properties = "server.port=8081")
public class UserControllerIntegrationTest {

    @Value("${server.port}")
    private int serverPort;

//    実行時に割り当てられた HTTP サーバーポートを取得
    @LocalServerPort
    private int localServerPort;

    @Autowired
//    統合テストに適したHTTP リクエストを実行する同期クライアント
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoads() {
        System.out.println(serverPort);
        System.out.println(localServerPort);
    }

    @Test
    @DisplayName("ユーザー作成時にユーザー情報が返されることを確認する")
    void test_create_user_return_user() throws JSONException {
//        Arrange
//        String createUserJson = "{\n" +
//                "  \"firstName\": \"Taro\",\n" +
//                "  \"lastName\": \"Tanaka\",\n" +
//                "  \"email\": \"test3@test.com\",\n" +
//                "  \"password\": \"12345678\",\n" +
//                "  \"repeatPassword\": \"12345678\"\n" +
//                "}";

//        JSONObject userDetailsRequestJson = new JSONObject(createUserJson);
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstName", "Taro");
        userDetailsRequestJson.put("lastName", "Tanaka");
        userDetailsRequestJson.put("email", "test@test.com");
        userDetailsRequestJson.put("password", "12345678");
        userDetailsRequestJson.put("repeatPassword", "12345678");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

//        Act
        ResponseEntity<UserRest> createdUserDetailsEntity = testRestTemplate.postForEntity(
                "/users", request, UserRest.class);
        UserRest createdUserDetails = createdUserDetailsEntity.getBody();

//        Assert
        Assertions.assertEquals(HttpStatus.OK, createdUserDetailsEntity.getStatusCode());
        Assertions.assertNotNull(createdUserDetails);
        Assertions.assertEquals(userDetailsRequestJson.getString("firstName"),
                createdUserDetails.getFirstName());
        Assertions.assertEquals(userDetailsRequestJson.getString("lastName"),
                createdUserDetails.getLastName());
        Assertions.assertEquals(userDetailsRequestJson.getString("email"),
                createdUserDetails.getEmail());
        Assertions.assertNotNull(createdUserDetails.getUserId());
        Assertions.assertFalse(createdUserDetails.getUserId().trim().isEmpty(),
                () -> "User ID should not be Empty");
    }
}

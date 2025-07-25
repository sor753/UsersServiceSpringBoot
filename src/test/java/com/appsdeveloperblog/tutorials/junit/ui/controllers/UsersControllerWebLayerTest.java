package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.service.UsersServiceImpl;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Webまわりのコンフィグレーションだけ行うように制限したDIコンテナを作成する
// ()の中には、テスト対象のControllerクラスを指定する
@WebMvcTest(controllers = UsersController.class,
excludeAutoConfiguration = {SecurityAutoConfiguration.class})
// MockMvcを使うためのアノテーション
// addFilters = false は、Spring SecurityのFilter Chainを無効化
//@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    UsersService usersService;

    @Test
    @DisplayName("ユーザー作成")
    void test_create_user_success() throws Exception {
//        Arrange
        UserDetailsRequestModel userDetails = new UserDetailsRequestModel();
        userDetails.setFirstName("Taro");
        userDetails.setLastName("Tanaka");
        userDetails.setEmail("taro@taro.com");
        userDetails.setPassword("12345678");
        userDetails.setRepeatPassword("12345678");

//        UserDto userDto = new UserDto();
//        userDto.setFirstName(userDetails.getFirstName());
//        userDto.setLastName(userDetails.getLastName());
//        userDto.setEmail(userDetails.getEmail());
//        userDto.setUserId(UUID.randomUUID().toString());

        UserDto userDto = new ModelMapper().map(userDetails, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetails));

//        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createdUser = new ObjectMapper()
                .readValue(responseBodyAsString, UserRest.class);

//        Assert
        Assertions.assertEquals(userDetails.getFirstName(),
                createdUser.getFirstName(),
                () -> "First name should match");
        Assertions.assertEquals(userDetails.getLastName(),
                createdUser.getLastName(),
                () -> "Last name should match");
        Assertions.assertEquals(userDetails.getEmail(),
                createdUser.getEmail(),
                () -> "Email should match");
        Assertions.assertFalse(createdUser.getUserId().isEmpty(),
                () -> "User ID should not be Empty");

    }

    @Test
    @DisplayName("First nameが空の場合、ユーザー作成失敗")
    void test_create_user_first_name_empty() throws Exception {
//        Arrange
        UserDetailsRequestModel userDetails = new UserDetailsRequestModel();
        userDetails.setFirstName("");
        userDetails.setLastName("Tanaka");
        userDetails.setEmail("taro@taro.com");
        userDetails.setPassword("12345678");
        userDetails.setRepeatPassword("12345678");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetails));

//        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

//        Assert
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(),
                () -> "HTTP Status should be 400 Bad Request");
    }

    @Test
    @DisplayName("First nameが1文字の場合、ユーザー作成失敗")
    void test_create_user_first_name_too_short() throws Exception {
//        Arrange
        UserDetailsRequestModel userDetails = new UserDetailsRequestModel();
        userDetails.setFirstName("T");
        userDetails.setLastName("Tanaka");
        userDetails.setEmail("taro@taro.com");
        userDetails.setPassword("12345678");
        userDetails.setRepeatPassword("12345678");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetails));

//        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

//        Assert
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(),
                () -> "HTTP Status should be 400 Bad Request");
    }

    @Test
    @DisplayName("First nameが2文字の場合、ユーザー作成成功")
    void test_create_user_first_name_too_short_success() throws Exception {
//        Arrange
        UserDetailsRequestModel userDetails = new UserDetailsRequestModel();
        userDetails.setFirstName("Ta");
        userDetails.setLastName("Tanaka");
        userDetails.setEmail("taro@taro.com");
        userDetails.setPassword("12345678");
        userDetails.setRepeatPassword("12345678");

        UserDto userDto = new ModelMapper().map(userDetails, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetails));

//        Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

//        Assert
        Assertions.assertEquals(200, mvcResult.getResponse().getStatus(),
                () -> "HTTP Status should be 200 OK");
    }
}

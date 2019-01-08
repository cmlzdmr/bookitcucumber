package com.bookit.utilities;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class RestUtils {
	
	
	public enum UserType{
		TEACHER, MEMBER,LEADER
	}
	
	
	public static String accessToken(UserType type) {
		
		//get tokenurl from configuration file
		String url = ConfigurationReader.getProperty("qa1_tokenurl");
		
		String email =null;
		String password = null;
		
		
		
		switch (type) {
		case TEACHER:
			
			email= ConfigurationReader.getProperty("qa1_teacher_apiuser");
			password= ConfigurationReader.getProperty("qa1_teacher_apipassword");
			break;
		case MEMBER:

			email= ConfigurationReader.getProperty("qa1_lead_username");
			password= ConfigurationReader.getProperty("qa1_lead_password");
			
			break;
		case LEADER:
			
			email= ConfigurationReader.getProperty("qa1_member_username");
			password= ConfigurationReader.getProperty("qa1_member_password");
			
			break;
		}
		
		Map<String,String> loginInf = new HashMap<>();
		loginInf.put("email", email);
		loginInf.put("password",password);		
				
		//first we need to get access token 
		Response response = given().accept(ContentType.JSON).and().params(loginInf).get(url);
		
		//status assertion
		assertEquals(response.statusCode(), 200);
		
		JsonPath jsonData = response.jsonPath();
		String token = jsonData.getString("accessToken");
		System.out.println(token);
		return token;

	}

}

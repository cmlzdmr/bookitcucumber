package com.bookit.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class HRRestAPIGetRequests {

	/*
	 When I send a GET request to url/emplooyees
	 Then response status code should be 200
	 *
	 */
	
	
	
	@Test
  public void simpleGet() {
	  
	  when().get("http://34.230.53.157:1000/ords/hr/employees")
	  .then().statusCode(200);
	  
	}
	
	/*
	 When I send a GET request to url/countries
	 Then I should see JSON response 
	 *
	 */
	
	@Test
	public void  printResponse() {
		when().get("http://34.230.53.157:1000/ords/hr/countries")
		.body().prettyPrint();
	}
	/*	
	 * When I send a GET request to REST Api Url
	And Accept type is "application/json"
	Then response status code should be 200
	*/
	@Test
	public void getWithHeaders() {
		
		given().accept(ContentType.JSON).
		when().get("http://34.230.53.157:1000/ords/hr/countries").then().statusCode(200);
	}
	
	/*
	 When I Send a GET request to URL/employees/1234
	 Then status code is 404
	 *
	 */
	@Test
	public void negativeGet() {
		when().get("http://34.230.53.157:1000/ords/hr/employees/1234")
		.then().statusCode(404);
		
	}
	
	
	/*
	 When I Send a GET request to URL/employees/1234
	 Then status code is 404
	 And Response body error message includes "Not Found" 
	 *
	 */
	@Test
	public void negativeGetResponse() {
		Response response = when().get("http://34.230.53.157:1000/ords/hr/employees/1234");
		
		//once we get the response we can do assertion
		assertEquals(response.statusCode(), 404);
		//check not found inside or not 
		assertTrue(response.asString().contains("Not Found"));
		//printint body 
		response.prettyPrint();
			
	}
	
      /*
        When I Send a GET request to URL/employees/110
		 And Accept type is json
		 Then status code is 200
		 And Response Content should be json 
       */
	
	@Test
	public void verifyContentTypeWithAssertThat() {
		//once jsonla dene sonra xml ile deneyince hata verecek
		//status code u da deneyebiliriz 
		
		
		given().accept(ContentType.JSON)
		.when().get("http://34.230.53.157:1000/ords/hr/employees/110")
		.then().assertThat().statusCode(200).and().contentType(ContentType.JSON);
		
		//before everything from get everything is our request after get everthing is response.
		
	}
	
	
	/*Given Accept type is JSON
	 * When I Send a GET request to URL/employees/110
	 * Then status code is 200
	 * And Response content should be json
	 * and first name should be " John"
	 * */
	
	
	@Test 
	public void verifyFirstName() {
		
		given().accept(ContentType.JSON)
		.when().get("http://34.230.53.157:1000/ords/hr/employees/110")
		.then().assertThat().statusCode(200).and().contentType(ContentType.JSON)
		.and().body("first_name", Matchers.equalTo("John"));
				   // body first argument looks for key and second argument we can check equality"
	}
	
	/*Given Accept type is JSON
	 * When I Send a GET request to URL/employees/110
	 * Then status code is 200
	 * And Response content should be json
	 * and first name should be " John"
	 * and employee_Id 110
	 * */
	
	
	@Test 
	public void verifyFirstNameAndEmpID() {
		
		given().accept(ContentType.JSON)
		.when().get("http://34.230.53.157:1000/ords/hr/employees/110")
		.then().assertThat().statusCode(200).and().contentType(ContentType.JSON)
		.and().body("first_name", Matchers.equalTo("John"))
		.and().body("employee_id", Matchers.equalTo(110));
				   // body first argument looks for key and second argument we can check equality"
	}
	
}

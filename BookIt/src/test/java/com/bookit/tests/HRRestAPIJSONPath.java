package com.bookit.tests;

import org.testng.annotations.Test;

import com.bookit.utilities.ConfigurationReader;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;
import  static org.hamcrest.Matchers.*;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class HRRestAPIJSONPath {
	
	//API CLASS 3 4 HOURS CLASS
	
	/*Given the Accept type is JSON
	  When I send a GET request to REST URL /regions
	 * Then status code is 200
	 * And Response content should be json
	 * and 4 Regions should be returned
	 * and has America
	 * and has america europe 
	 */
	
	//when we say items.region_id it is getting multipple items so we can use hassize has item and items 
	@Test
  public void testItemsCountFromResponseBody() {
		given().accept(ContentType.JSON).
		when().get(ConfigurationReader.getProperty("hrapp.baseurl")+"/regions")
		.then().assertThat().statusCode(200)
		.and().contentType(ContentType.JSON)
		.and().assertThat().body("items.region_id", hasSize(4))
		.and().assertThat().body("items.region_id", hasItem("Europe"))
		.and().assertThat().body("items.region_name", hasItems("Americas","Europe","do"));
  }
	
	/*Given the Accept type is JSON
	  When I send a GET request to REST URL /regions
	 * Then status code is 200
	 * And Response content should be json
	 * and second region name is Americas
	 * 
	 */
	
	@Test
public void verifyRegionName() {
		given().accept(ContentType.JSON).
		when().get(ConfigurationReader.getProperty("hrapp.baseurl")+"/regions")
		.then().assertThat().statusCode(200)
		.and().contentType(ContentType.JSON)
		.and().assertThat().body("items[1].region_name", equalTo("Americas"));
}
	
	/*
	 Given Accept type is JSON 
	 When I send a GET request to employees
	 Then status code is 200
	 And Response content should be json
	 And 100 employees data should be in json response body
	 */
	
	@Test
	public void testWithQueryParametersAndList() {
		given().accept(ContentType.JSON)
		.and().params("limit",100)
		.when().get(ConfigurationReader.getProperty("hrapp.baseurl")+"/employees")
		.then().assertThat().statusCode(200)
		.and().contentType(ContentType.JSON)
		.and().assertThat().body("items.employee_id", hasSize(100));
		
	}
	
	/*
	 Given Accept type is JSON 
	 And Params are limit=100
	 And Path param is 110
	 When I send a GET request to employees
	 Then status code is 200
	 And Response content should be json
	 And following data should be return 
	 "employee_id": 110,
	 "first_name" "john",
	 "last_name":"Chen",
	 "email":"JCHEN",
	 */
	
	@Test
	public void testWithPathParameters() {
		given().accept(ContentType.JSON).and().params("limit",100)
		.and().pathParams("id", 110)
		.when().get(ConfigurationReader.getProperty("hrapp.baseurl")+"/employees/{id}") //when we send e request this id will be replaced with 110
		.then().statusCode(200)
		.and().assertThat().contentType(ContentType.JSON)
		.and().body("employee_id", equalTo(110),
				"first_name",equalTo("John"),
				"last_name",equalTo("Chen"),
				"email",equalTo("JCHEN"));
		
	}
	
	//================================JSON PATH ====================
	/*
	 Given Accept type is JSON 
	 And Params are limit=100
	 And Path param is 110
	 When I send a GET request to employees
	 Then status code is 200
	 And Response content should be json
	 And following data should be return 
	 "employee_id": 110,
	 "first_name" "john",
	 "last_name":"Chen",
	 "email":"JCHEN",
	 */
	@Test
	public void testWithJsonPath() {
				
		Response response = given().accept(ContentType.JSON)
		.and().pathParam("id", 110)
		.when().get(ConfigurationReader.getProperty("hrapp.baseurl")+"/employees/{id}");
		
		JsonPath json = response.jsonPath();  //get json body and assign to jsonPath object
		//we are assiging whole response to json object so we can easyly reach and use it 
		
		System.out.println(json.getInt("employee_id"));
		System.out.println(json.getString("first_name"));
		System.out.println(json.getString("job_id"));
		System.out.println(json.getInt("salary"));
		System.out.println(json.getString("links[0].href")); //get specific element from array 
	   
		//assign all hrefs into a list of strings 
		List<String> hrefs = json.getList("links.href");
		
		//everything in arraylist 
	    System.out.println(hrefs);
	    
	    //one by one 
		for(String href:hrefs) {
			System.out.println(href);
		}
	}
	
	
	/*
	 Given Accept type is JSON 
	 And Params are limit=100
	 When I send a GET request to employees
	 Then status code is 200
	 And Response content should be json
	 And all data should be return 
	 */
	
	@Test 
	public void testJsonPathWithLists() {
	
		Map<String,Integer> requestParamMaps=new HashMap<>();
		 requestParamMaps.put("limit", 100);
		
			Response response = given().accept(ContentType.JSON).and().params(requestParamMaps)
			.when().get(ConfigurationReader.getProperty("hrapp.baseurl")+"/employees/");
			
			assertEquals(response.statusCode(), 200);
			
			JsonPath json = response.jsonPath();
			//get all employee ids into arratlist
			List<Integer> empIDs =  json.getList("items.employee_id");
			System.out.println(empIDs);
			//assert that there are 100 emp ids
			assertEquals(empIDs.size(), 100);
			
			//get all emails and assign in list 
			List<String> emails = json.getList("items.email");
			
			assertEquals(emails.size(), 100);
			
			//get all employee ids that are greater than 150
			List<Integer> empIdList = json.getList("items.findAll{it.employee_id > 150}.employee_id");
			System.out.println(empIdList);
			
			//get all employee lastnames, whose salary is more than 7000
			
			List<String> lastNames = json.getList("items.findAll{it.salary > 7000}.last_name");
			System.out.println(lastNames);
	}
	
	
	//working with json file 
	@Test
	public void readJSONFromFile() {
		
		JsonPath jsonFile = new JsonPath(new File ("C:\\Users\\cmlzd\\Desktop\\employees.json"));
		
		System.out.println(jsonFile.getString("items.email"));
		
		
	}
	
	
	
	
	
	
}











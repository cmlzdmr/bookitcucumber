package com.bookit.tests;

import org.testng.annotations.Test;

import com.bookit.utilities.ConfigurationReader;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
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

public class HRRestAPI_GSON {
  @Test
  public void testWithJSONtoHashMap(){
		
			Response response = given().accept(ContentType.JSON).and()
			.when().get(ConfigurationReader.getProperty("hrapp.baseurl")+"/employees/120");
			
			//we convert JSON result to hashmap data structure
			Map<String,String> map = response.as(HashMap.class);
			//getting whole map
			System.out.println(map);
			//all keys
			System.out.println(map.keySet());
			//all values
			System.out.println(map.values());
			
			//compare job id 
			assertEquals(map.get("job_id"), "ST_MAN");
			
  }
  
  
  @Test
  public void convertJSONtoListOfMaps() {

		Response response = given().accept(ContentType.JSON).and()
		.when().get(ConfigurationReader.getProperty("hrapp.baseurl")+"/departments");
		//convert the response that contains departments infromation into list of maps 
		
		List<Map> listofMaps = response.jsonPath().getList("items",Map.class);
		
		System.out.println(listofMaps.get(0));
		
		//assert that first deparment name is "Administration"
		assertEquals(listofMaps.get(0).get("department_name"),"Administration");
		
		//we learned how to converty JSON to list of maps 
	
  }
  
  	/*
  	 * Given Content type is JSON
  	 * And limit is 10
  	 * When I send request to Rest API url:
  	 * url/regions
  	 * the status code must be 200
  	 * Then I should see following data:
  	 * 			1 Europe
  	 * 			2 Americas
  	 * 			3 Asia
  	 * 			4 Middile East and Africa
  	 */
  @Test
  public void testRegionsJSON() {
	  Map<String,Integer> requestParamMap = new HashMap<>();
	  		requestParamMap.put("limit", 10);
	  		
	  
	  Response response = given()
	  		.accept(ContentType.JSON).params(requestParamMap).
	  when()
	  		.get(ConfigurationReader.getProperty("hrapp.baseurl")+"/regions");
	  	
	  	//status code verification 
	  	assertEquals(response.statusCode(), 200);
	  
	    //data verification with JsonPath
	  	
	  	JsonPath jsonData = response.jsonPath();
	  	
	  	assertEquals(jsonData.getString("items[0].region_name"), "Europe");
	  	assertEquals(jsonData.getString("items[1].region_name"), "Americas");
	  	assertEquals(jsonData.getString("items[2].region_name"), "Asia");
	  	assertEquals(jsonData.getString("items[3].region_name"), "Middle East and Africa");
  }
  
  @Test
  public void testRegionsLISTANDMAPS() {
	  Map<String,Integer> requestParamMap = new HashMap<>();
	  		requestParamMap.put("limit", 10);
	  		
	  
	  Response response = given()
	  		.accept(ContentType.JSON).params(requestParamMap).
	  when()
	  		.get(ConfigurationReader.getProperty("hrapp.baseurl")+"/regions");
	  	
	  	//status code verification 
	  	assertEquals(response.statusCode(), 200);
	  
	    //data verification with List and Maps	
	  	List<Map> listOfRegions = response.jsonPath().getList("items",Map.class);
	  	
	  	assertEquals(listOfRegions.get(0).get("region_name"), "Europe");
	  	assertEquals(listOfRegions.get(1).get("region_name"), "Americas");
	  	assertEquals(listOfRegions.get(2).get("region_name"), "Asia");
	  	assertEquals(listOfRegions.get(3).get("region_name"), "Middle East and Africa");
	  	
  }
}

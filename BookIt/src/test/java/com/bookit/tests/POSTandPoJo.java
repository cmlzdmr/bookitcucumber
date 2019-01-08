package com.bookit.tests;

import org.testng.annotations.Test;

import com.bookit.beans.Country;
import com.bookit.beans.CountryResponse;
import com.bookit.beans.Regions;
import com.bookit.beans.RegionsResponse;
import com.bookit.utilities.ConfigurationReader;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.synth.Region;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class POSTandPoJo {
	
	/*given content type is JSON 
	And accept type is Json 
	When I send POST request to
	url/Regionswith Request Body

	{

	 "region_id": 5,
	 "region_name" : "Jamal's Regions"

	}

	Then status code should be 201
	and response body should match request body */
  
	//first version with string body 
	@Test
  public void postNewRegion() {
		
		String url = ConfigurationReader.getProperty("hrapp.baseurl")+"/regions/";
		//not a good way 
		String requestBody = "{\"region_id\": 5,\"region_name\" : \"Jamal's Regions\"}";
				
		Response response = given().
			accept(ContentType.JSON).and()
			.contentType(ContentType.JSON).and().body(requestBody)
		.when().post(url);
		
		
		System.out.println(response.statusLine());
		response.prettyPrint();
		
  }
	
	@Test
	  public void postNewRegionV2() {
			
			String url = ConfigurationReader.getProperty("hrapp.baseurl")+"/regions/";
			//not a good way 
			//String requestBody = "{\"region_id\": 5,\"region_name\" : \"Jamal's Regions\"}";
				
			Map requestMap = new HashMap<>();
			
			requestMap.put("region_id", 6);			
			requestMap.put("region_name", "My region");
			
			//map will be converted to JSON
			Response response = given().
				accept(ContentType.JSON).and()
				.contentType(ContentType.JSON).and().body(requestMap)
			.when().post(url);
			
			JsonPath jsonData = response.jsonPath();
			
			assertEquals(response.statusCode(), 201);
			assertEquals(jsonData.getInt("region_id"), requestMap.get("region_id"));
			assertEquals(jsonData.getString("region_name"), requestMap.get("region_name"));
			
			//puting body to map 
			
			Map responseMap = response.body().as(Map.class);
			

			assertEquals(responseMap.get("region_id"), requestMap.get("region_id"));
			assertEquals(responseMap.get("region_name"), requestMap.get("region_name"));
	  }
	
	/*
	 * MAP -> JAVA OBJECT
		When we send post request , what do we send in request body?
		
		JSON request body.
		
		JAVA OBJECT --> JSON
		
		HASHMAP -> JSON
		Serialization
		*/
	
	//creating pojo for regions 

	@Test
	  public void postNewRegionV3POJO() {
			
			String url = ConfigurationReader.getProperty("hrapp.baseurl")+"/regions/";
			
			//creating POJO object for Request body 
			Regions region = new Regions();
			
			region.setRegion_id(7);
			region.setRegion_name("test region");
			
			
			//map will be converted to JSON
			Response response = given().
				accept(ContentType.JSON).and()
				.contentType(ContentType.JSON).and().body(region)
			.when().post(url);
			
			//Creating pojp object for response body 
			RegionsResponse responseRegion = response.body().as(RegionsResponse.class);
			
			assertEquals(response.statusCode(), 201);
			//request and response body assertion with POJO 
			assertEquals(responseRegion.getRegion_id(), region.getRegion_id());
			assertEquals(responseRegion.getRegion_name(), region.getRegion_name());
						
			
			
	  }
	
	/*TASK
	given content type is Json
	And Accept type is Json
	When I send POST request to 
	URL/countries/
	with request body :
	{
	"country_id": "AR",
	"country_name": "Argentina",
	"region_id": 2
	}

	Then status code should be 201
	And response body should match request body
	*/
	
	@Test
	
	public void countriesPOSTPOJO() {
		String url = ConfigurationReader.getProperty("hrapp.baseurl")+"/countries/";

		Country reqCountry = new Country();
		reqCountry.setCountry_id("AA");
		reqCountry.setCountry_name("Jamals Country");
		reqCountry.setRegion_id(2);
		
		Response response = given().accept(ContentType.JSON).and()
				.contentType(ContentType.JSON).and().
				body(reqCountry).when().post(url);
		
		CountryResponse responseCountry = response.body().as(CountryResponse.class);
		
		//status assertion 
		assertEquals(response.statusCode(), 201);
		
		//assertion for request body 
		
		assertEquals(responseCountry.getCountry_id(), reqCountry.getCountry_id());
		assertEquals(responseCountry.getCountry_name(), reqCountry.getCountry_name());
		assertEquals(responseCountry.getRegion_id(), reqCountry.getRegion_id());
		
	}
	
	
	@Test
		public void countriesPUT() {
		String url = ConfigurationReader.getProperty("hrapp.baseurl")+"/countries/AA";

		Country reqCountry = new Country();
		reqCountry.setCountry_id("AA");
		reqCountry.setCountry_name("Jamals News Country");
		reqCountry.setRegion_id(2);
		
		Response response = given().accept(ContentType.JSON).and().log().all()
				.contentType(ContentType.JSON).and().
				body(reqCountry).when().put(url);
		
		CountryResponse responseCountry = response.body().as(CountryResponse.class);
		
		//status assertion 
		assertEquals(response.statusCode(), 200);
		
		//assertion for request body 
		
		assertEquals(responseCountry.getCountry_id(), reqCountry.getCountry_id());
		assertEquals(responseCountry.getCountry_name(), reqCountry.getCountry_name());
		assertEquals(responseCountry.getRegion_id(), reqCountry.getRegion_id());
		
	}
	

	@Test
	public void countriesDELETE() {
	String url = ConfigurationReader.getProperty("hrapp.baseurl")+"/countries/AA";

	Response response = given().accept(ContentType.JSON)
			.and().when().delete(url);
		
	//status assertion 
	assertEquals(response.statusCode(), 200);

	JsonPath jsonData = response.jsonPath();
	
	//check succesfuly deleted 
	assertEquals(jsonData.getInt("rowsDeleted"), 1);
	
}
	
	
	
}

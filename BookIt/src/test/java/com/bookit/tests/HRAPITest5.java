package com.bookit.tests;

import org.testng.annotations.Test;
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
import java.util.Random;

import javax.swing.plaf.synth.Region;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
public class HRAPITest5 {

	/*
	 * Given Content Type and Accept Type is JSON
	 * When I post a new Employee with "1234" id 
	 * Then Status code is 201
	 * And Response JSON should contain employee info
	 */

	
	
	@Test
  public void newEmployeePOST() {
		int randomID = new Random().nextInt(999);
		String url = ConfigurationReader.getProperty("hrapp.baseurl")+"/employees/";
		
		Map reqEmployee = new HashMap();
		reqEmployee.put("employee_id", randomID);
		reqEmployee.put("first_name","POSTNAME");
		reqEmployee.put("last_name","POSTLNAME");
		reqEmployee.put("email","EM"+randomID);
		reqEmployee.put("phone_number", "202.123.4567");
		reqEmployee.put("hire_date","2018-04-24T07:25:00Z");
		reqEmployee.put("job_id","IT_PROG");
		reqEmployee.put("salary",7000);
		reqEmployee.put("commission_pct",null);
		reqEmployee.put("manager_id",null);
		reqEmployee.put("department_id",90);
		

		//Given Content type and Accept type is Json
		Response response = given().accept(ContentType.JSON)
							.and().contentType(ContentType.JSON)
							.and().body(reqEmployee)
							.when().post(url);
		
		assertEquals(response.statusCode(),201);
		
		Map<String,Object> postResEmployee = response.body().as(Map.class);
		//	And Response Json should contain Employee info
		for(Object key : reqEmployee.keySet()) {
			System.out.println(postResEmployee.get(key)+" <> "+reqEmployee.get(key));
			assertEquals(postResEmployee.get(key),reqEmployee.get(key));
		}
		
		/*When i send a GET request with "1100" id 
		Then status code is 200
		And employee JSON Response Data should match the posted JSON data
		*/
		
		response = given().accept(ContentType.JSON)
				   .when().get(url+randomID);
		
		assertEquals(response.statusCode(),200);
		
		Map getResMap = response.body().as(Map.class);
		
		for(Object key:reqEmployee.keySet()) {
			System.out.println(key + ": " +reqEmployee.get(key) + "<>" +getResMap.get(key));
			assertEquals(getResMap.get(key),reqEmployee.get(key));
		}
  }
}

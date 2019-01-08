package com.bookit.step_definitions;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bookit.pages.SelfPage;
import com.bookit.pages.SigninPage;
import com.bookit.utilities.BrowserUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Driver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class threePointVerificationStepDefs {

	String email;
	String jsonData;
	JsonPath jsonBody;
	Response res;
	List<Map<String,Object>> result;
	
	@Given("user logs in UI using {string} {string}")
	public void user_logs_in_UI_using(String username, String password) {
		Driver.getDriver().get(ConfigurationReader.getProperty("qa1_url"));
		Driver.getDriver().manage().window().maximize();
		SigninPage signInPage = new SigninPage();
	   signInPage.email.sendKeys(username);
	   signInPage.password.sendKeys(password);
	   signInPage.signInButton.click();
	   System.out.println();
	   
	}
	
	@Then("the user logs in api with {string} and {string}")
	public void the_user_logs_in_api_with_and(String username, String password) {
		email = username;
		//get tokenurl from configuration file
		String url = ConfigurationReader.getProperty("qa1_tokenurl");
		
		Map<String,String> loginInf = new HashMap<>();
		loginInf.put("email", username);
		loginInf.put("password",password);
		
		
		//first we need to get access token 
		Response response = given().accept(ContentType.JSON).and().params(loginInf).get(url);
		
		//status assertion
		assertEquals(response.statusCode(), 200);
		
		JsonPath jsonData = response.jsonPath();
		String token = jsonData.getString("accessToken");
		//we got the token 
		System.out.println(token);
		
		//============================================================
		
		String urlbase= ConfigurationReader.getProperty("qa1_baseurl")+"/students/me";

		Response res = given().header("Authorization",token).and().when().get(urlbase);
		
		//check status 
		assertEquals(res.statusCode(), 200);
		
		//assign body to json
		 jsonBody = res.jsonPath();
	}
	
//	@When("the user is on the my self page")
//	public void the_user_is_on_the_my_self_page() {
//	    SelfPage selfPage = new SelfPage();
//	    selfPage.goToSelf();
//	}
	
	
	@Then("UI, Database and Api records should match")
	public void ui_Database_and_Api_records_should_match() {
		String sql = "SELECT users.email, users.firstname, users.lastname,users.role, team.name as teamname, team.batch_number as batchnumber,campus.location\r\n" + 
				"    FROM users INNER JOIN  team\r\n" + 
				"        ON users.team_id = team.id\r\n" + 
				"        INNER JOIN campus \r\n" + 
				"        ON team.campus_id =campus.id\r\n" + 
				"        where email = '"+email+"'";

		 result = DBUtils.getQueryResultMap(sql);
		//per requirements, we cannot have duplicated emails 
		assertEquals("returned mulipte users with email:", 1, result.size());
		
		//getting information from and assigning to variable 
		String expectedFirstName = (String) result.get(0).get("firstname");
		String expectedLastName = (String) result.get(0).get("lastname");
		String expectedRole = (String) result.get(0).get("role");
		String expectedTeam = (String) result.get(0).get("teamname");
		int expectedBatchNumber = (int) result.get(0).get("batchnumber");
		String expectedLocation = (String) result.get(0).get("location");
		
		//converting to full name 
		String expectedFullname = expectedFirstName+" "+expectedLastName;
		//==============================================================
	    SelfPage selfPage = new SelfPage();
	    //update yuklenene kadar bekle 
	    
	    BrowserUtils.waitFor(2);
	    String aFullName=selfPage.name.getText();
	    String aRole = selfPage.role.getText();
	    String aTeam = selfPage.team.getText();
	    String aBatchNumber = selfPage.batch.getText().split("#")[1];
	    String aLocation = selfPage.campus.getText();
	    
	    //UI To DATABASE        
	    assertEquals("Names are not matching",expectedFullname, aFullName);
	    assertEquals("Roles are not matching",expectedRole, aRole);
	    assertEquals("Teams are not matching",expectedTeam,aTeam);
	    assertEquals("Batch numbers are not mathcing", expectedBatchNumber,Integer.parseInt(aBatchNumber));
	    assertEquals("locations are not matching", expectedLocation,aLocation);
	    
	    //API to Database 
	     String apiFirstName = jsonBody.getString("firstName");
	     String apiLastName = jsonBody.getString("lastName");
	     String apiRole = jsonBody.getString("role");
	     
	     //DATABASE TO API
	     
	     assertEquals(expectedFirstName, apiFirstName);
	     assertEquals(expectedLastName, apiLastName);
	     assertEquals(expectedRole, apiRole);

	     //UI to API
	     String apiFullname = apiFirstName+" "+ apiLastName;
	     assertEquals(aFullName, apiFullname);
	     assertEquals(aRole, apiRole);
	}

}

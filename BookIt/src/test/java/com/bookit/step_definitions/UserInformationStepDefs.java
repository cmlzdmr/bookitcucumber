package com.bookit.step_definitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
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

public class UserInformationStepDefs {

	String token;
	String fullname;
	Response res;
	JsonPath jsonData;
	JsonPath jsonBody;
	String email;
	List<Map<String,Object>> result;
	Map<Object,Object> respondMap;
	
	
@Given("I am logged BookIt api using {string} and {string}")
public void i_am_logged_BookIt_api_using_and(String username, String password) {

	fullname = password;
	email=username;
	
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
	token = jsonData.getString("accessToken");
	//we got the token 
	System.out.println(token);
	
	//============================================================
	
}

@When("I get the curret user information user the api service")
public void i_get_the_curret_user_information_user_the_api_service() {

	String url= ConfigurationReader.getProperty("qa1_baseurl")+"/students/me";

	Response res = given().header("Authorization",token).and().when().get(url);
	
	//check status 
	assertEquals(res.statusCode(), 200);
	
	//assign body to json
	 jsonData = res.jsonPath();

	
}

@Then("the information about current user should be returned")
public void the_information_about_current_user_should_be_returned() {
	
		
		String actualFullname = jsonData.getString("firstName").toLowerCase()+jsonData.getString("lastName").toLowerCase();
		
		//verify that the password which consist of firstnameandlastname matches with the result firstname and lastname
		assertEquals(actualFullname, fullname);
}

@Given("I am logged BookIt api as teacher")
public void i_am_logged_BookIt_api_as_teacher() {
	
	//get tokenurl from configuration file
	String url = ConfigurationReader.getProperty("qa1_tokenurl");
	
	Map<String,String> loginInf = new HashMap<>();
	loginInf.put("email", ConfigurationReader.getProperty("qa1_teacher_apiuser"));
	loginInf.put("password",ConfigurationReader.getProperty("qa1_teacher_apipassword"));
	
	
	//first we need to get access token 
	Response response = given().accept(ContentType.JSON).and().params(loginInf).get(url);
	
	//status assertion
	assertEquals(response.statusCode(), 200);
	
	JsonPath jsonData = response.jsonPath();
	token = jsonData.getString("accessToken");
	//we got the token 
	System.out.println(token);
	
	//============================================================
}

@When("I get the user information by id {int} using the student endpoint")
public void i_get_the_user_information_by_id_using_the_student_endpoint(Integer id) {
	
	String url = ConfigurationReader.getProperty("qa1_baseurl")+"/students/"+id;
			
	Response res = given().header("Authorization",token).and().when().get(url);
	//status check
	assertEquals(res.statusCode(), 200);
	
	//assign body to json data
	
	jsonData = res.jsonPath();
	
	respondMap = res.jsonPath().getMap("$");

}

@Then("the correct user information should be returned")
public void the_correct_user_information_should_be_returned(Map<String,String> user) {
   
	String expectedFirstName = (String)user.get("firstName");
	String expectedLastName = (String)user.get("lastName");
	String expectedID = (String)user.get("id");
	String expectedRole = (String)user.get("role");
	
	String actualFirstName = jsonData.getString("firstName");
	String actualLastName = jsonData.getString("lastName");
	String actualRole = jsonData.getString("role");
	String actualId = jsonData.getString("id");
	
	assertEquals(actualFirstName, expectedFirstName);
	assertEquals(actualLastName, expectedLastName);
	assertEquals(actualId, expectedID);
	assertEquals(actualRole, expectedRole);
	
	//respondMap.put("id", respondMap.get("id")+"");
	
	
	//comparing with the MAP TOOOO
	for(String key: user.keySet()) {
		assertEquals(user.get(key), respondMap.get(key)+"");
	}	
	
}


@Then("the information about current user should be match with the user table on database")
public void the_information_about_current_user_should_be_match_with_the_user_table_on_database() {
	
	String query ="select  id,firstname,lastname,role\r\n" + 
			"from users\r\n" + 
			"where email= '"+email+"'";
	
   List<Map<String,Object>> userDB = DBUtils.getQueryResultMap(query);
   System.out.println(userDB);
   	
     
     
   //verify database result with api result 
      assertEquals(userDB.get(0).get("firstname"), jsonData.get("firstName"));
      assertEquals(userDB.get(0).get("lastname"), jsonData.get("lastName"));
      assertEquals(userDB.get(0).get("role"), jsonData.get("role"));
      assertEquals(userDB.get(0).get("id").toString(), jsonData.get("id").toString());


}



@Then("user info should match the all DB records {string}")
public void user_info_should_match_the_all_DB_records(String username) {
	String sql = "SELECT users.email, users.firstname, users.lastname,users.role, team.name as teamname, team.batch_number as batchnumber,campus.location\r\n" + 
			"    FROM users INNER JOIN  team\r\n" + 
			"        ON users.team_id = team.id\r\n" + 
			"        INNER JOIN campus \r\n" + 
			"        ON team.campus_id =campus.id\r\n" + 
			"        where email = '"+username+"'";

	 result = DBUtils.getQueryResultMap(sql);
	//per requirements, we cannot have duplicated emails 
	assertEquals("returned mulipte users with email:", 1, result.size());
	
	//getting information and assigning to variable 
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
    
}







}

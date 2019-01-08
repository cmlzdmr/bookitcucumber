package com.bookit.step_definitions;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.RestUtils;
import com.bookit.utilities.RestUtils.UserType;
import com.github.javafaker.Faker;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserRegistrationStepDefs {

	
	Response res;
	String token;
	String emailDB;
	String emailFake;
	
	@Given("I am logged BookIt api as team lead")
	public void i_am_logged_BookIt_api_as_team_lead() {
		
		token = RestUtils.accessToken(UserType.LEADER);
	
	}
	
	@Given("I am logged BookIt api as team member")
	public void i_am_logged_BookIt_api_as_team_member() {
		token = RestUtils.accessToken(UserType.MEMBER);
	}
	
	@Given("I am logged BookIt api as a teacher")
	public void i_am_logged_BookIt_api_as_a_teacher() {
	   token = RestUtils.accessToken(UserType.TEACHER);
	}

	@When("I try to register a new user")
	public void i_try_to_register_a_new_user() {
	 
		//we need query params
		
		String url = ConfigurationReader.getProperty("qa1_baseurl")+"/students/student";
		
		Map<String,String> postParams = new HashMap<>();
		//postParams.put("first-name", "dami");
		postParams.put("last-name", "Mapam");
		postParams.put("email", "jamesmayjr@gmail.com");
		postParams.put("password", "terimapam");
		postParams.put("role", "dami");
		postParams.put("batch-number", "8");
		postParams.put("team-name", "dami");
		postParams.put("campus-location", "dami");
		
		res= given().header("Authorization",token).params(postParams)
				.when().post(url);
		
		
		
	}
	
	@Then("system should return only teachers can register message")
	public void system_should_return_only_teachers_can_register_message() {
		//verify status code is 403
				assertEquals("Verify status code", res.statusCode(),403);	
				
		//verfiy message is correct 
		String expectedMessage = "only teacher allowed to modify database.";
		String actualMessage = res.body().asString();
		
		assertEquals("Verify expected message",expectedMessage,actualMessage);
		System.out.println(expectedMessage+"<>"+actualMessage);
	}


	@Then("the teacher should be authorised to add users")
	public void the_teacher_should_be_authorised_to_add_users() {
		//verify status code is 422
		assertEquals("Verify status code", res.statusCode(),422);
		
		//verfiy message is correct 
		String expectedMessage = "pay attention to how you specifying first-name in the query.";
		String actualMessage = res.body().asString();
		
		assertEquals("Verify expected message",expectedMessage,actualMessage);
		System.out.println(expectedMessage+"<>"+actualMessage);
	}

	@When("I try to register a new user with existing email")
	public void i_try_to_register_a_new_user_with_existing_email() {
	    //go to database and get a existing user
		//using the user information from database, create new query parameter
		
		String query = "SELECT email FROM users\r\n" + 
				"where email is not null\r\n" + 
				"limit 1";
		emailDB = (String) DBUtils.getCellValue(query);
		assertNotNull(emailDB); //we make sure db returning some email 
		System.out.println(emailDB);
		//==========================================================
		
		String url = ConfigurationReader.getProperty("qa1_baseurl")+"/students/student";
		
		Map<String,String> postParams = new HashMap<>();
		postParams.put("first-name", "Mike");
		postParams.put("last-name", "Smith");
		postParams.put("email", emailDB);
		postParams.put("password", "terimapam");
		postParams.put("role", "student-team-member");
		postParams.put("batch-number", "8");
		postParams.put("team-name", "TheCrew");
		postParams.put("campus-location", "VA");
		
		res= given().header("Authorization",token).params(postParams)
				.when().post(url);
		
		
		
	}

	@Then("user with smae email exists message should be returned")
	public void user_with_smae_email_exists_message_should_be_returned() {
		//verify status code is 422
		assertEquals("Verify status code", res.statusCode(),422);
		
		//verfiy message is correct 
		String expectedMessage = "user with the email: "+emailDB+" is already exist.";
		String actualMessage = res.body().asString();
		
		assertEquals("Verify expected message",expectedMessage,actualMessage);
		System.out.println(expectedMessage+"<>"+actualMessage);
	}
	
	@When("I register a new user")
	public void i_register_a_new_user() {
		Faker faker = new Faker();
		String emailFake = "test123@gamil.coam";
		
		String url = ConfigurationReader.getProperty("qa1_baseurl")+"/students/student";
		
		Map<String,String> postParams = new HashMap<>();
		postParams.put("first-name", faker.name().firstName());
		postParams.put("last-name", faker.name().lastName());
		postParams.put("email", emailFake);
		postParams.put("password", "terimapam");
		postParams.put("role", "student-team-member");
		postParams.put("batch-number", "8");
		postParams.put("team-name", "TheCrew");
		postParams.put("campus-location", "VA");
		
		res= given().header("Authorization",token).params(postParams)
				.when().post(url);
	}

	@Then("new user should registered")
	public void new_user_should_registered() {
	
		assertEquals("Verify status code", res.statusCode(),500);	

		//verify with database 
		String query= "select email from users where email ='"+emailFake+"'";
		
		String actualEmail = (String) DBUtils.getCellValue(query);
		
		assertEquals("Verify new email is on the database",emailFake, actualEmail);
		
		//verify status code is 403
		assertEquals("Verify status code", res.statusCode(),201);	
		
		//verfiy message is correct 
		String expectedMessage = "only teacher allowed to modify database.";
		String actualMessage = res.body().asString();
		
		assertEquals("Verify expected message",expectedMessage,actualMessage);
		System.out.println(expectedMessage+"<>"+actualMessage);
	}

}

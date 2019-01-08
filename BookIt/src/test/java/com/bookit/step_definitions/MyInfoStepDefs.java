package com.bookit.step_definitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebElement;

import com.bookit.pages.SelfPage;
import com.bookit.pages.SigninPage;
import com.bookit.pages.TeamPage;
import com.bookit.utilities.BrowserUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Driver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class MyInfoStepDefs {
	
	@Given("user logs in using {string} {string}")
	public void user_logs_in_using(String username, String password) {
	   
		Driver.getDriver().get(ConfigurationReader.getProperty("qa1_url"));
		Driver.getDriver().manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Driver.getDriver().manage().window().maximize();
		SigninPage signInPage = new SigninPage();
	   signInPage.email.sendKeys(username);
	   signInPage.password.sendKeys(password);
	   signInPage.signInButton.click();
	   System.out.println();
	   
	}
	
	@When("the user is on the my self page")
	public void the_user_is_on_the_my_self_page() {
	    SelfPage selfPage = new SelfPage();
	    selfPage.goToSelf();
	}
	
	@Then("user info should match the database records {string}")
	public void user_info_should_match_the_database_records(String email) {
			
			//writing our query 
			String query = "Select firstname,lastname,role from users\r\n" + 
						"where email = '"+email+"'";
			
			//assign result to map since it is one row 
			Map<String,Object> result = DBUtils.getRowMap(query);
						
			//getting information and assigning to variable 
			String expectedFirstName = (String) result.get("firstname");
			String expectedLastName = (String) result.get("lastname");
			String expectedRole = (String) result.get("role");
			
			//converting to full name 
			String expectedFullname = expectedFirstName+" "+expectedLastName;
			//==============================================================
		    SelfPage selfPage = new SelfPage();
		    
		    //wait until self page loaded 
		    BrowserUtils.waitFor(2);
		    String aFullName=selfPage.name.getText();
		    String aRole = selfPage.role.getText();
		    
		    assertEquals("Names are not matching",expectedFullname, aFullName);
		    assertEquals("Roles are not matching",expectedRole, aRole);

	}
	
	@When("the user is on the my team page")
	public void the_user_is_on_the_my_team_page() {
		 SelfPage selfPage = new SelfPage();
		    selfPage.goToTeam();
		    BrowserUtils.waitFor(2);
		    
	}

	@Then("team info should match the database records {string}")
	public void team_info_should_match_the_database_records(String email) {
	    TeamPage teamPage= new TeamPage();
	    
	    //getting team member names from front-end
	    List<String> actualNames =new ArrayList<>();
	    for(WebElement el:teamPage.teamMemberNames){
	    	actualNames.add(el.getText());
	    }
	    
	    //getting roles from front-end 
	    List<String> actualRoles =new ArrayList<>();

	    for(WebElement el:teamPage.teamMemberRoles) {
	    	
	    	actualRoles.add(el.getText());
	    }
	    
	    //the query returns team member of given email
		String sql = "select * from users\r\n" + 
				"where team_id = (select team_id from users where email = '"+email+"')";
		
		//assign query result to list of maps 
		List<Map<String,Object>> result = DBUtils.getQueryResultMap(sql);
			
		//before one by one comparison, checking number of members from UI and DB
			assertEquals("team member numbers must be matching",result.size(), actualNames.size());
			
			//for each member from database, does our ui result includes that name ?
			for(Map<String,Object> map:result) {
				String firstName = (String) map.get("firstname");
				assertTrue(actualNames.contains(firstName));
			}
			
			//for each role from database does our ui includes that role ?
			for(Map<String,Object> map:result) {
				String role = (String) map.get("role");
				assertTrue(actualRoles.contains(role));
			}
			
			
		System.out.println(actualNames);
		System.out.println(actualRoles);
	}
	
	@Then("user info should match the all database records {string}")
	public void user_info_should_match_the_all_database_records(String email) {

		String sql = "SELECT users.email, users.firstname, users.lastname,users.role, team.name as teamname, team.batch_number as batchnumber,campus.location\r\n" + 
				"    FROM users INNER JOIN  team\r\n" + 
				"        ON users.team_id = team.id\r\n" + 
				"        INNER JOIN campus \r\n" + 
				"        ON team.campus_id =campus.id\r\n" + 
				"        where email = '"+email+"'";
	
		
		Map<String,Object> queryResult = DBUtils.getRowMap(sql);
		
		
		//getting information and assigning to variable 
		String expectedFirstName = (String) queryResult.get("firstname");
		String expectedLastName = (String) queryResult.get("lastname");
		String expectedRole = (String) queryResult.get("role");
		String expectedTeam = (String) queryResult.get("teamname");
		int expectedBatchNumber = (int) queryResult.get("batchnumber");
		String expectedLocation = (String) queryResult.get("location");
		
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
	    
	    
	    
//	    System.out.println(expectedLocation);
//	    System.out.println(expectedTeam);
//	    System.out.println(expectedBatchNumber);
	    
	    assertEquals("Names are not matching",expectedFullname, aFullName);
	    assertEquals("Roles are not matching",expectedRole, aRole);
	    assertEquals("Teams are not matching",expectedTeam,aTeam);
	    assertEquals("Batch numbers are not mathcing", expectedBatchNumber,Integer.parseInt(aBatchNumber));
	    assertEquals("locations are not matching", expectedLocation,aLocation);
	    
	    
	}

}

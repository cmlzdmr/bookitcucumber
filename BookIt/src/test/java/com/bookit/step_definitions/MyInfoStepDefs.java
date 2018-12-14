package com.bookit.step_definitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	   
			String sql = "Select firstname,lastname,role from users\r\n" + 
						"where email = 'efewtrell8c@craigslist.org'";
		
			List<Map<String,Object>> result = DBUtils.getQueryResultMap(sql);
			//per requirements, we cannot have duplicated emails 
			assertEquals("returned mulipte users with email:", 1, result.size());
			
			//getting information and assigning to variable 
			String expectedFirstName = (String) result.get(0).get("firstname");
			String expectedLastName = (String) result.get(0).get("lastname");
			String expectedRole = (String) result.get(0).get("role");
			
			//convertin to full name 
			String expectedFullname = expectedFirstName+" "+expectedLastName;
			//==============================================================
		    SelfPage selfPage = new SelfPage();
		    //update yuklenene kadar bekle 
		    
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
	public void team_info_should_match_the_database_records(String string) {
	    TeamPage teamPage= new TeamPage();
	    
	    List<String> actualNames =new ArrayList<>();
	    for(WebElement el:teamPage.teamMemberNames){
	    	actualNames.add(el.getText());
	    }
	    
	    List<String> actualRoles =new ArrayList<>();

	    for(WebElement el:teamPage.teamMemberRoles) {
	    	
	    	actualRoles.add(el.getText());
	    }
	    
		String sql = "select * from users\r\n" + 
				"where team_id = (select team_id from users where email = 'efewtrell8c@craigslist.org')";
		
		List<Map<String,Object>> result = DBUtils.getQueryResultMap(sql);
			//comparing numbers 
			assertEquals("team member numbers must be matching",result.size(), actualNames.size());
			
			for(Map<String,Object> map:result) {
				String firstName = (String) map.get("firstname");
				assertTrue(actualNames.contains(firstName));
			}
			
			for(Map<String,Object> map:result) {
				String role = (String) map.get("role");
				assertTrue(actualRoles.contains(role));
			}
			
			
		System.out.println(actualNames);
		System.out.println(actualRoles);
	}
	
	@Then("user info should match the all database records {string}")
	public void user_info_should_match_the_all_database_records(String username) {

		String sql = "SELECT users.email, users.firstname, users.lastname,users.role, team.name as teamname, team.batch_number as batchnumber,campus.location\r\n" + 
				"    FROM users INNER JOIN  team\r\n" + 
				"        ON users.team_id = team.id\r\n" + 
				"        INNER JOIN campus \r\n" + 
				"        ON team.campus_id =campus.id\r\n" + 
				"        where email = '"+username+"'";
	
		List<Map<String,Object>> result = DBUtils.getQueryResultMap(sql);
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
	    
	    
	    
	    System.out.println(expectedLocation);
	    System.out.println(expectedTeam);
	    System.out.println(expectedBatchNumber);
	    
	    assertEquals("Names are not matching",expectedFullname, aFullName);
	    assertEquals("Roles are not matching",expectedRole, aRole);
	    assertEquals("Teams are not matching",expectedTeam,aTeam);
	    assertEquals("Batch numbers are not mathcing", expectedBatchNumber,Integer.parseInt(aBatchNumber));
	    assertEquals("locations are not matching", expectedLocation,aLocation);
	    
	    
	}

}

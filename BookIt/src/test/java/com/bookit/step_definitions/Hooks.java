package com.bookit.step_definitions;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Driver;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class Hooks {

//	@Before
//	public void setUp() {
//		Driver.getDriver().manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//		Driver.getDriver().manage().window().maximize();
//	
//	}
	
	@Before("@db")
	public void setUpDBConnection() {
		DBUtils.createConnection();
	}
	
	@After("db")
	public void tearDownDBConnection() {
		DBUtils.destroy();
	}
	
	@After
	public void tearDown(Scenario scenario) {
		//only takes a screenshot if the scenario fails 
		if(scenario.isFailed()) {
			//taking screenshot
		final byte[] screenshot = ((TakesScreenshot) Driver.getDriver()).getScreenshotAs(OutputType.BYTES);
		
		scenario.embed(screenshot,  "image/png");
		}
		Driver.closeDriver();
	}
}

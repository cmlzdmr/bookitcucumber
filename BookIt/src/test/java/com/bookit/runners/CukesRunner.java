package com.bookit.runners;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"html:target/default-cucumber-reports",
				  "json:target/cucumber.json",
				  "pretty"
		
		},
		tags = "@temp",
		features="src/test/resources/com/bookit/features",
		glue="com/bookit/step_definitions",
		dryRun=false
		
		)
public class CukesRunner {

}

Feature: Information about me

#@temp @db
Scenario: my self 
	Given user logs in using "efewtrell8c@craigslist.org" "jamesmay"
	When the user is on the my self page
	Then user info should match the database records "efewtrell8c@craigslist.org"
	

Scenario: my self 
	Given user logs in using "efewtrell8c@craigslist.org" "jamesmay"
	When the user is on the my team page
	Then team info should match the database records "efewtrell8c@craigslist.org"
	

Scenario: my self 
	Given user logs in using "efewtrell8c@craigslist.org" "jamesmay"
	When the user is on the my self page
	Then user info should match the all database records "efewtrell8c@craigslist.org"

@temp @db	
Scenario Outline: multiple user my self test 
	Given user logs in using "<username>" "<password>"
	When the user is on the my self page
	Then user info should match the all database records "<username>"
	Examples:
	|username				   |password 		 |
	|efewtrell8c@craigslist.org|jamesmay		 |
	|jrowesby8h@google.co.uk   |aldridgegrimsdith|
	|bmurkus8q@psu.edu		   |alicasanbroke	 |
	
	
	 
	
	
	
	
	
	
	
	
	
	
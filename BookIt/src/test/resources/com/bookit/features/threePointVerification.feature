Feature: Api Database and UI verification

	@api @db  
Scenario Outline: multiple user my self test ui api and database verificiation
	Given user logs in UI using "<username>" "<password>"
	And the user logs in api with "<username>" and "<password>"
	When the user is on the my self page
	Then UI, Database and Api records should match  
	
	Examples:
	|username				   |password 		 |
	|efewtrell8c@craigslist.org|jamesmay		 |
	|jrowesby8h@google.co.uk   |aldridgegrimsdith|
	|bmurkus8q@psu.edu		   |alicasanbroke	 |
	|strayford84@e-recht24.de  |carlosmichie	 |
	
	
Feature: User information 


Scenario: Verify information about the logged in user
	Given I am logged BookIt api using "teacherva4@gmail.com" and "markwohlberg"
	When I get the curret user information user the api service
	Then the information about current user should be returned 
	
@api 
Scenario: Verify user by id 
	Given I am logged BookIt api as teacher 
	When I get the user information by id 40 using the student endpoint
	Then the correct user information should be returned
	|id|40|
	|firstName|Angie|
	|lastName|Coatham|
	|role|student-team-member|
	

@api @db 
Scenario: Verify information about the logged in user with the database
	Given I am logged BookIt api using "teacherva4@gmail.com" and "markwohlberg"
	When I get the curret user information user the api service
	Then the information about current user should be match with the user table on database 
	
	
	
	
Scenario Outline: Verify information about the logged in user with database
	Given I am logged BookIt api using "<username>" and "<password>"
	When I get the curret user information user the api service
	Then the information about current user should be match with the user table on database 
	Examples:
	|username					|password	  	|
	|daldie7l@seattletimes.com	|ruthannjohnes	|
	|teacherva5@gmail.com		|maxpayne	  	|
	|csummergill83@blinklist.com|edycaton	  	|
	|sutting7v@liveinternet.ru  |leonardwarfield|
	
	

	
	
	
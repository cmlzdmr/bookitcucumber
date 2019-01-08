Feature: User Registration 

@api @db 
Scenario: Permissions verification: team leader
	Given I am logged BookIt api as team lead 
	When I try to register a new user 
	Then system should return only teachers can register message 
	

@api @db
Scenario: Permissions verification: team member
	Given I am logged BookIt api as team member 
	When I try to register a new user 
	Then system should return only teachers can register message 
	
@api @db 
Scenario: Permissions verification: teacher
	Given I am logged BookIt api as a teacher
	When I try to register a new user 
	Then the teacher should be authorised to add users 
@api @db 	 
Scenario: Verify existing user email
	Given I am logged BookIt api as a teacher
	When I try to register a new user with existing email
	Then user with smae email exists message should be returned 

@api @db 	 
Scenario: Create new user 
	Given I am logged BookIt api as a teacher
	When I register a new user
	Then new user should registered
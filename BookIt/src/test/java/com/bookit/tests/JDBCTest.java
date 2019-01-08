package com.bookit.tests;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.bookit.utilities.DBUtils;

public class JDBCTest {
	
	String dbUrl = "jdbc:postgresql://localhost:5432/hr";
	String dbUsername = "postgres";
	String dbPassword = "abc";
	
	
	@Test(enabled=false)
  public void PostGReSQL() throws SQLException {
	  
	  Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
	  Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	  ResultSet resultSet = statement.executeQuery("SELECT * FROM countries");
	  
//	  while(resultSet.next()) {
//	  System.out.println(resultSet.getString(1) +" "+ resultSet.getString("country_name"));
//	  }
	  
	  resultSet.next();
	  
	  resultSet.last();
	  System.out.println(resultSet.getRow());
	  resultSet.absolute(20);
	  System.out.println(resultSet.getRow());
	  resultSet.previous();
	  System.out.println(resultSet.getRow());

	  
	  resultSet.close();
	  statement.close();
	  connection.close();
	  
  }
	
	
	@Test(enabled=false)
	public void JDBCMetadata() throws SQLException {
		 
		  Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		  Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		  ResultSet resultSet = statement.executeQuery("SELECT * FROM departments");
		  
		  //database metadata(create object)
		  DatabaseMetaData dbMetadata = connection.getMetaData();
		  
		  //which username we are using ?
		  System.out.println("User:"+dbMetadata.getUserName());
		  //database product name 
		  System.out.println("Database Product Name:"+ dbMetadata.getDatabaseProductName());
		  //database product version 
		  System.out.println("Database Product version:"+ dbMetadata.getDatabaseProductVersion());
			  
		  
		  //ResultSet MetaData(create object)
		  ResultSetMetaData rsMetadata = resultSet.getMetaData();
		  
		  //how many column we have?(not row COLUMN)
		  System.out.println("Columns count: "+rsMetadata.getColumnCount());
		  //get column name 
		  System.out.println(rsMetadata.getColumnName(1));
		  //get table name
		  System.out.println(rsMetadata.getTableName(1));
		  
		  //Print all column names using a loop 
		  
		  int columnCount = rsMetadata.getColumnCount();
		  
		  for(int i=1;i<columnCount;i++) {
			  System.out.println(rsMetadata.getColumnName(i));
		  }
		  
		  
		  
		  
		  resultSet.close();
		  statement.close();
		  connection.close();
		  
	}
	
	@Test(enabled=false)
	public void DbUtils() throws SQLException {
		  Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		  Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		  ResultSet resultSet = statement.executeQuery("Select employee_id,first_name,last_name,job_id\n" + 
		  		"From employees\n" + 
		  		"Limit 5");
		  
		  //our main structure, it will keep whole query result 
		  //we have empty list of maps 
		  List<Map<String,Object>> queryData = new ArrayList<>();
		  
		  //we will add the first row data to this list with creating map
		  Map<String,Object> row1 = new HashMap<>();
		  //key is colum name, value is that column value 
		  resultSet.next(); //we are at first row 
		  row1.put("employee_id",resultSet.getObject("employee_id"));
		  row1.put("first_name", resultSet.getObject("first_name"));
		  row1.put("last_name", resultSet.getObject("last_name"));
		  row1.put("job_id",resultSet.getObject("job_id"));
		  
		  //we added first row columns and its values 
		  //print map and see 
		  System.out.println(row1.toString());
		  //Result {job_id=AD_PRES, employee_id=100, last_name=King, first_name=Steven}
		  
		  //add this map to list 
		  queryData.add(row1);
		  
		  //print list to check
		  System.out.println(queryData.get(0));
		  
		  //if we want to see first name 
		  System.out.println(queryData.get(0).get("first_name")); //Steven
		  //------------------------------Adding other rows-------------------
		  Map<String,Object> row2 = new HashMap<>();
		  //key is colum name, value is that column value 
		  resultSet.next(); //we are at second row 
		  row2.put("employee_id",resultSet.getObject("employee_id"));
		  row2.put("first_name", resultSet.getObject("first_name"));
		  row2.put("last_name", resultSet.getObject("last_name"));
		  row2.put("job_id",resultSet.getObject("job_id"));
		  
		  //add second row map to list 
		  queryData.add(row2);
		  
		  //check second row  
		  System.out.println(queryData.get(1)); 
		  //------------------DYNAMIC LIST FOR EVERY QUERY-----------------------
		  
		 /*
		  * To have dynamic list what we need to do is we need to know
		  * how many columns we have,what are the column names and its values
		  * we will put each row column names and values into one map
		  * and add that map to list until we covered all rows in the query result*/
		  
		  //Main list
		  List<Map<String,Object>> queryList = new ArrayList<>();
		  
		  //to get column names and column count, we will use ResultSet MetaData
		  ResultSetMetaData rsMetaData = resultSet.getMetaData();
		  
		  //total column numbers
		  int colCount = rsMetaData.getColumnCount();
		  
		  //we will use loop to iterate each row 
		  
		  while(resultSet.next()){
			  //each time we will create map 
			  Map<String,Object> row = new HashMap<>();
			  
			  //how to add column names and values dynamicly ? 
			  
			  //looping each row starting from left to right (example emp_id,first,last..)
			  for(int col=1; col<=colCount;col++) {
				  //we are getting column name from metadata and passing to key 
				  row.put(rsMetaData.getColumnName(col), resultSet.getObject(col));
				  //getting column value with resulSet object and passing as a value 
			  }
			  			  
			  //after adding all column values to that map we will add that one to list 
			  queryList.add(row);
		  }
		  
		 //print all employee ids from list 
		  
		  for(Map<String,Object> emp: queryList) {
			  System.out.println(emp.get("employee_id"));
		  }
		  
		  resultSet.close();
		  statement.close();
		  connection.close();
	}
	
	@Test
	public void testWithDBUtils(){
		
		//create connection with given information from configuration files
		DBUtils.createConnection(); 
		
		String query = "Select first_name,last_name,salary,job_id FROM employees";
		
		List<Map<String,Object>> queryResult = DBUtils.getQueryResultMap(query);
		
		//reaching first row first name value
		System.out.println(queryResult.get(1).get("first_name"));
		
		//loop trough each value in first_name column
		
		for(Map<String,Object> row : queryResult) {
			
			System.out.println(row.get("first_name"));
		}
		
		//close connection 
		DBUtils.destroy();
	}
	
	@Test
	public void testWithDBUtils2() throws Exception{
		
		//create connection with given information from configuration files
		DBUtils.createConnection(); 
		
		String query = "Select first_name,last_name,salary,job_id FROM employees WHERE emp_id = 110";
		
		//assigning just one row as a result 
		Map<String,Object> queryResult = DBUtils.getRowMap(query);
		
		//reaching first_name value 
		System.out.println(queryResult.get("first_name"));
		
		
		//close connection 
		DBUtils.destroy();
	}
	
	
}







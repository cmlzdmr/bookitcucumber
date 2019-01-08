package com.bookit.beans;

import java.util.ArrayList;
import java.util.Map;

public class CountryResponse {
	private String country_id;
	private String country_name;
	private int region_id;
	private ArrayList<Map<String,String>> links;
	
	public String getCountry_id() {
		return country_id;
	}
	public void setCountry_id(String country_id) {
		this.country_id = country_id;
	}
	public String getCountry_name() {
		return country_name;
	}
	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}
	public int getRegion_id() {
		return region_id;
	}
	public void setRegion_id(int region_id) {
		this.region_id = region_id;
	}
	public ArrayList<Map<String, String>> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Map<String, String>> links) {
		this.links = links;
	} 
	
	
	
}

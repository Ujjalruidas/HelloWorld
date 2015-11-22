package com.tcs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

public class HelloWorld {
	
	public static void main(String[] args) {
		
		String parcelShops_api="https://psfinder-test.hermesworld.com/psfinder-rest-api-impl/rest/";
		
		String url = parcelShops_api+"findParcelShopsByLocation?countries=UK&consumerName=EXT000297&consumerPassword=d648192c6a10968896fa1c68dcd71d18&lat=53&lng=-1&maxDist=1000000&maxResult=2&geo=true";
		
		try {
			URL	ur = new URL(url);
			URLConnection urlCon;
		    urlCon = ur.openConnection();
			
			JsonReader rdr = Json.createReader(urlCon.getInputStream());
			JsonArray recs = rdr.readArray();
			System.out.println(recs);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		
		
	}

}

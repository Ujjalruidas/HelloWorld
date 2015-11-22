package com.tcs;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonException;
import javax.json.JsonString;

import JsonData.JsonData;


public class Test {
	
	 static Map map1 = new HashMap();
	 
	  int count = 0;
	  
	  
	
	
	public static void main(String[] args) {
		
		String name = "Abcd#";
		
		Test t = new Test();
		
		
//		Pattern pattern = Pattern.compile("[/,:<>!~@#$%^&()+=?()\"|!\\[#$-]");
//		

//	    Matcher matcher = pattern.matcher(name);
//	    
//
//	    if (matcher.matches()) {
//	        System.out.println("Match");
//	    } else {
//	        System.out.println("Not Matching");
//	    }
//		
		
		
//		String patternToMatch = "[\\\\!\"#$%&()*+,./:;<=>?@\\[\\]^_{|}~]+";
//		Pattern p = Pattern.compile(patternToMatch);
//		Matcher m = p.matcher(name);
//		boolean characterFound = m.find();
//		
		
//		System.out.println(t.splCharCheck(name));		
//		System.out.println(t.isNumeric(name));
		
		
		
		
		String Country = "India";
		
		String searchTerm = "London";
		
		String postalcode = "999999";
		
//		String url = "http://maps.google.com/maps/api/geocode/json?components=country:"+Country+"|postal_code:"+postalcode+"&sensor=false";
			
		
//		String url = "http://maps.google.com/maps/api/geocode/json?address="+searchTerm+"&sensor=false";
		String parcelShops_api="https://psfinder-test.hermesworld.com/psfinder-rest-api-impl/rest/";
		
		String url = parcelShops_api+"findParcelShopsByLocation?countries=UK&consumerName=EXT000297&consumerPassword=d648192c6a10968896fa1c68dcd71d18&lat=53&lng=-1&maxDist=1000000&maxResult=2&geo=true";
		
		List<JDWLocation> locationList = new ArrayList<JDWLocation>();
		JsonData jsonData = new JsonData();
		
		 JDWLocation jdwLocation = new JDWLocation();
		
		
		
		
		
		
		
try{
			
			URL	ur = new URL(url);
			URLConnection urlCon;
//			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.tcs.com", 8080));
			
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("", 8080));
//			Proxy proxy = Proxy.NO_PROXY;
			urlCon = ur.openConnection();
//			urlCon = ur.openConnection();
			
			JsonReader rdr = Json.createReader(urlCon.getInputStream());
			JsonObject object = null;
			JsonObject object2 = null;
			String street="";
			String postCode="";
			String city = "";
			String telephone = "";
			JsonNumber dist ;
			JsonNumber parcelShopNumber ;
		    JsonNumber lat ;
		    JsonNumber lng ;
		    
		    JsonArray businessHours ;
		    String dayOfWeek= "";
		    String openFrom= "";
		    String openTill= "";
		    
					 
//			JsonObject obj = rdr.readObject();
			
			JsonArray recs = rdr.readArray();
			System.out.println(recs);
			
//			JsonArray recs = obj.getJsonArray("results");
			for(int n = 0; n < recs.size(); n++)
			{
				jsonData = new JsonData();
				jdwLocation = new JDWLocation();
			    object = recs.getJsonObject(n);
//			    System.out.println(object);
			    JsonObject address_components = object.getJsonObject("address");
//			    System.out.println(address_components);
			     street = address_components.getString("street");
			     postCode = address_components.getString("postCode");
			     city = address_components.getString("city");
			     telephone = address_components.getString("telephone");
			     
			     parcelShopNumber = object.getJsonNumber("parcelShopNumber");
			     
			     	businessHours = object.getJsonArray("businessHours");
			     	
			     	for(int u = 0; u < businessHours.size(); u++)
					{
			     		object2 = businessHours.getJsonObject(u);
			     		
			     		dayOfWeek = object2.getString("dayOfWeek");
			     		openFrom = object2.getString("openFrom");
			     		openTill = object2.getString("openTill");
//			     		System.out.println(" dayOfWeek "+dayOfWeek);
			     		
					}
			     	
			     	
			     	
//			     	dayOfWeek = businessHours.getObject("dayOfWeek");
			     
			     
			     
			     lat =object.getJsonNumber("lat");
			     lng =object.getJsonNumber("lng");
			     dist = object.getJsonNumber("dist");
			     
			     jdwLocation.setGeometry_location_lat(lat.toString());
			     jdwLocation.setGeometry_location_lng(lng.toString());
			     jdwLocation.setCity(city);
			     jdwLocation.setDescription(street);
			     jdwLocation.setZip(postCode);
			     jdwLocation.setDistrict(dist.toString());
			     jdwLocation.setTelephone(telephone);
			     jdwLocation.setParcelShopNumber(parcelShopNumber.toString());
			     
			     locationList.add(jdwLocation);
			}
			
			
			
//			for (int i = 0; i < locationList.size(); i++) {
//				JDWLocation jd = locationList.get(i);
//				
//				System.out.println("City "+jd.getCity()+"  short_name "+jd.getDescription()+"  lat "+jd.getGeometry_location_lat()+"    lng "+jd.getGeometry_location_lng()+" Parcel Shop  "+jd.getParcelShopNumber()+"  Telephone"+jd.getTelephone()+"  Zip "+jd.getZip());
//			}
			    
			    
			   
//			    
//			    JsonObject object3 = object2.getJsonObject("location");
//			    
//			    JsonNumber lat = object3.getJsonNumber("lat");
//			    
//			    JsonNumber lng = object3.getJsonNumber("lng");
//			    
//			    jsonData.setGeometry_location_lat(lat.toString());
//			    jsonData.setGeometry_location_lng(lng.toString());
//			    
//			    
//			    for(int u = 0; u < address_components.size(); u++)
//				{
//			    	JsonObject object1 = address_components.getJsonObject(u);
//			    	
//			    	 String long_name = object1.getString("long_name");	
//			    	 String short_name = object1.getString("short_name");
//			    	 if(u==0){jsonData.setLong_name1(long_name);}			    	 
//			    	 if(u==1){jsonData.setLong_name2(long_name);}			    	 
//			    	 if(u==2){jsonData.setLong_name3(long_name);}	
//			    	 if(u==3){jsonData.setLong_name4(long_name);}	
//			    	 
//			    	 if(u==0){jsonData.setShort_name1(short_name);}			    	 
//			    	 if(u==1){jsonData.setShort_name2(short_name);}			    	 
//			    	 if(u==2){jsonData.setShort_name3(short_name);}	
//			    	 if(u==3){jsonData.setShort_name4(short_name);}	
//			    	
//				}			    
//			    locationList.add(jsonData);
			    
//			}
			
			
			
			
//			for (int i = 0; i < locationList.size(); i++) {
//				
//				JsonData jd = locationList.get(i);
//				
//				System.out.println("long_name "+jd.getLong_name4()+"  short_name "+jd.getShort_name4()+"  lat "+jd.getGeometry_location_lat()+"    lng "+jd.getGeometry_location_lng());
//			}
			
			
		}  catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		

		
//		try{
//			
//			URL	ur = new URL(url);
//			URLConnection urlCon;
//			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.tcs.com", 8080));
//			urlCon = ur.openConnection(proxy);
//			
//			JsonReader rdr = Json.createReader(urlCon.getInputStream());
//					 
//			JsonObject obj = rdr.readObject();
//			
//			
//			JsonArray recs = obj.getJsonArray("results");
//			for(int n = 0; n < recs.size(); n++)
//			{
//				jsonData = new JsonData();
//			    JsonObject object = recs.getJsonObject(n);
//			    JsonArray address_components = object.getJsonArray("address_components");
//			    
//			    JsonObject object2 = object.getJsonObject("geometry");
//			    
//			    JsonObject object3 = object2.getJsonObject("location");
//			    
//			    JsonNumber lat = object3.getJsonNumber("lat");
//			    
//			    JsonNumber lng = object3.getJsonNumber("lng");
//			    
//			    jsonData.setGeometry_location_lat(lat.toString());
//			    jsonData.setGeometry_location_lng(lng.toString());
//			    
//			    
//			    for(int u = 0; u < address_components.size(); u++)
//				{
//			    	JsonObject object1 = address_components.getJsonObject(u);
//			    	
//			    	 String long_name = object1.getString("long_name");	
//			    	 String short_name = object1.getString("short_name");
//			    	 if(u==0){jsonData.setLong_name1(long_name);}			    	 
//			    	 if(u==1){jsonData.setLong_name2(long_name);}			    	 
//			    	 if(u==2){jsonData.setLong_name3(long_name);}	
//			    	 if(u==3){jsonData.setLong_name4(long_name);}	
//			    	 
//			    	 if(u==0){jsonData.setShort_name1(short_name);}			    	 
//			    	 if(u==1){jsonData.setShort_name2(short_name);}			    	 
//			    	 if(u==2){jsonData.setShort_name3(short_name);}	
//			    	 if(u==3){jsonData.setShort_name4(short_name);}	
//			    	
//				}			    
//			    locationList.add(jsonData);
//			    
//			}
//			
//			
//			
//			
////			for (int i = 0; i < locationList.size(); i++) {
////				
////				JsonData jd = locationList.get(i);
////				
////				System.out.println("long_name "+jd.getLong_name4()+"  short_name "+jd.getShort_name4()+"  lat "+jd.getGeometry_location_lat()+"    lng "+jd.getGeometry_location_lng());
////			}
//			
//			
//		}  catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		
//		
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
		
		
	
		
	}
	
	
	
	public  List<Object> getListFromJsonArray(JsonArray jArray) throws JsonException {
	      List<Object> returnList = new ArrayList<Object>();
	      
	     
	      for (int i = 0; i < jArray.size(); i++) {
	    	  
	        returnList.add(convertJsonItem(jArray.get(i)));
	      }
	      return returnList;
	    }
	
	
	   public int getCount() {
		return count;
	}



	public void setCount(int count) {
		this.count = count;
	}



	public  Object convertJsonItem(Object o) throws JsonException {
		      if (o == null) {
		        return "null";
		      }

		      if (o instanceof JsonObject) {
		        return getListFromJsonObject((JsonObject) o);
		      }

		      if (o instanceof JsonArray) {
		        return getListFromJsonArray((JsonArray) o);
		      }

		      if (o.equals(Boolean.FALSE) || (o instanceof String &&
		          ((String) o).equalsIgnoreCase("false"))) {
		        return false;
		      }

		      if (o.equals(Boolean.TRUE) || (o instanceof String && ((String) o).equalsIgnoreCase("true"))) {
		        return true;
		      }

		      if (o instanceof Number) {
		        return o;
		      }

		      return o.toString();
		    }
	   
	   
	   public  List<Object> getListFromJsonObject(JsonObject jObject) throws JsonException {
		   
		  
		   Map map2 = new HashMap();
		 
		      List<Object> returnList = new ArrayList<Object>();
		      Iterator keys =  jObject.keySet().iterator();

		      List<String> keysList = new ArrayList<String>();
		      while (keys.hasNext()) {
		        keysList.add(keys.next().toString());
//		        System.out.println("  next"+keys.next().toString());
		      }
		      Collections.sort(keysList);

		      for (String key : keysList) {
		        List<Object> nestedList = new ArrayList<Object>();
		        
		      
		        	
		         if(key.equalsIgnoreCase("address_components")){
		        	System.out.println("key "+key);
		        	this.count++;
		        }
//		         if(key.equalsIgnoreCase("lat")){
//		        	System.out.println(" key "+key);
//		        }
		        
		        System.out.println("key "+key);
		         map1.put("address_components["+count+"]."+key, jObject.get(key));
		        
		         if(key.equalsIgnoreCase("location")){
		        	 map1.put("address_components["+count+"]."+key, jObject.get(key));
		        	 
		         }
		         
		        nestedList.add(key);
		        
//		        System.out.println("  key "+key);
		        nestedList.add(convertJsonItem(jObject.get(key)));
		        returnList.add(nestedList);
		       
		      }

		      return returnList;
		    }
	
	
	public static String callURL(String myURL) {
		System.out.println("Requested URL:" + myURL);
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.tcs.com", 8080));
			//urlConn = url.openConnection();
			urlConn = url.openConnection(proxy);
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:"+ myURL, e);
		} 
 
		return sb.toString();
	}
	
	public boolean splCharCheck(String input){
		
		String patternToMatch = "[\\\\!\"#$%&()*+,./:;<=>?@\\[\\]^_{|}~]+";		
		Pattern p = Pattern.compile(patternToMatch);		
		Matcher m = p.matcher(input);		
		boolean characterFound = m.find();	
		return characterFound;
		
	}
	
	public boolean isNumeric(String input){
		String regex = "[0-9]+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		boolean characterFound = m.find();
		return characterFound;
	}
	
	public  static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
	
	
	
	
	
	
}

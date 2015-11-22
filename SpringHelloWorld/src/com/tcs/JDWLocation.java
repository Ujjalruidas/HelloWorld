package com.tcs;


//import de.hybris.platform.storelocator.GPS;
//import de.hybris.platform.storelocator.data.AddressData;
//import de.hybris.platform.storelocator.location.Location;

/**
 * @author 244817
 *
 */
public  class JDWLocation  {



	public String name;

	public String description;

//	public GPS GPS;
//
//	public AddressData addressData;

	public double distance;

	public String city;
	public String street;
	public String desc;
	public String zip;
	public String type;
	public String district;
	public String telephone;
	
	public String getDistrict() {
		return district;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	private String geometry_location_lat;
	private String geometry_location_lng;
	private String parcelShopNumber;

public String getParcelShopNumber() {
		return parcelShopNumber;
	}

	public void setParcelShopNumber(String parcelShopNumber) {
		this.parcelShopNumber = parcelShopNumber;
	}

public String getGeometry_location_lat() {
		return geometry_location_lat;
	}

	public void setGeometry_location_lat(String geometry_location_lat) {
		this.geometry_location_lat = geometry_location_lat;
	}

	public String getGeometry_location_lng() {
		return geometry_location_lng;
	}

	public void setGeometry_location_lng(String geometry_location_lng) {
		this.geometry_location_lng = geometry_location_lng;
	}

public String getCity() {
		return city;
	}

	public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



//	public GPS getGPS() {
//		return GPS;
//	}
//
//	public void setGPS(GPS gPS) {
//		GPS = gPS;
//	}
//
//	public AddressData getAddressData() {
//		return addressData;
//	}
//
//	public void setAddressData(AddressData addressData) {
//		this.addressData = addressData;
//	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}






}
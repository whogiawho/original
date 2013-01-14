package com.openfeint.internal;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationProxy {

	private static LocationProxy instance;
	
	public static LocationProxy getInstance(Context context){
		if(instance == null){
			instance = new LocationProxy(context);
		}
		return instance;
	}
	
	private Context context;
	private boolean gpsEnable;
	private Location location;
	private LocationProxy(Context context){
		this.context = context;
		init();
	}
	
	private void init(){
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if (location == null) {
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1800000, 100, new GpsLocationListener());
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1800000, 100, new NetworkLocationListener());
	}
	
	public Location getLocation(){
		return location;
	}
	
	
	private class GpsLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location _location) {
			location = _location;
		}

		@Override
		public void onProviderDisabled(String provider) {
			gpsEnable = false;
		}

		@Override
		public void onProviderEnabled(String provider) {
			gpsEnable = true;
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	}	
	
	private class NetworkLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location _location) {
			if(!gpsEnable){
				location = _location;
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	}
}

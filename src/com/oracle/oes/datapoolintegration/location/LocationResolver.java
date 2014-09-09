package com.oracle.oes.datapoolintegration.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the LocationResolverType, that resolves Locations from a
 * config file repository.
 * 
 * @author OCS
 * 
 */
public class LocationResolver implements LocationResolverType {

	// logger
	private transient Logger log = Logger.getLogger(LocationResolver.class.getName());

	// properties
	private Map<String, String> locationMap;

	private Properties props;

	// constructor
	public LocationResolver() {
		loadLocations();
		log.finest("new instance of LocationResolver");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * oesdatapoolintegration.location.LocationResolverType#getLocation(java.lang.String
	 * )
	 */
	@Override
	public String getLocation(String name) {
		return locationMap.get(name.toUpperCase());
	}

	/**
	 * Method for reading the all the configuration files.
	 */
	private void loadLocations() {
		log.finest("loadLocations - init");
		InputStream stream = LocationResolver.class.getResourceAsStream("/config/config.properties");
		if (stream != null) {
			try {
				props = new Properties();
				props.load(stream);
				log.finer("Properties file Content : " + props);
				locationMap = new HashMap<String, String>();
				String location = props.getProperty("locations");
				log.finer("Following location configurations = " + location);
				String[] locations = location.split(",");
				for (String filename : locations) {
					String locationArr = processFileName(filename).toString();
					if (locationArr != null) {
						locationArr = locationArr.replace("[", "").replace("]", "");
					}
					locationMap.put(filename.replace(".txt", "").toUpperCase(), locationArr);
				}
			} catch (IOException e) {
				log.severe("exception while getting location data from /config/config.properties.");
				log.log(Level.SEVERE, e.getMessage(), e);
				log.warning("location Data will not be loaded.");
			}
		} else {
			log.severe("please verify that /config/config.properties is in the classpath");
		}
		log.finest("loadLocations - end");
	}

	/**
	 * Process a file of locations
	 * 
	 * @param filename
	 *            Name of the file
	 * @return List of locations from the file
	 * @throws IOException
	 */
	private List<Location> processFileName(String filename) throws IOException {
		log.finest("processing location file:" + filename);
		String path = "/config/locations/" + filename;
		InputStream stream = LocationResolver.class.getResourceAsStream(path);
		log.finest("stream for path : " + path + " is:" + stream);
		List<Location> locations = new ArrayList<Location>();

		if (stream != null) {

			InputStreamReader isr = new InputStreamReader(stream);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			String[] array = null;

			while ((line = br.readLine()) != null) {
				array = line.split(" ");
				if (array.length > 3) {
					locations.add(new Location(array[0], array[1], array[2]));
				}
			}
			log.finest("list of locations in file " + filename + "=" + locations);

		} else {
			log.warning("location file not found : " + path);
			log.warning("please check the configuration file at config/config.properties");
		}
		return locations;
	}

}

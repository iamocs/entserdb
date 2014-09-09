package com.oracle.oes.datapoolintegration.parser;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import org.openliberty.openaz.azapi.pep.Obligation;

import com.oracle.oes.datapoolintegration.location.LocationResolverFactory;
import com.oracle.oes.datapoolintegration.location.LocationResolverType;

/**
 * Implementation of the interface ObligationParserType.
 * 
 * 
 */
public class ObligationParser implements ObligationParserType {

	private static Logger log = Logger.getLogger(ObligationParser.class.getName());

	private static final String POSITION_STR = "POSITION";

	private static final String WITHIN_STR = " WITHIN(LOCATION,GEOMFROMTEXT('POLYGON($1)')) ";

	private static final String OR_STR = " OR ";

	private static final String AND_STR = " AND ";

	private static final String EQUALS_STR = " = ";

	private static final String LEFT_STR = " ( ";

	private static final String RIGHT_STR = " ) ";

	public ObligationParser() {
		super();
		log.finest("new intance of ObligationParser");
	}

	private String getCoordinates(String name) {
		LocationResolverType locationResolver = LocationResolverFactory.getInstance();
		String location = locationResolver.getLocation(name);
		if (location == null) {
			location = "(0 0)";
			log.finer("no location for " + name);
		}
		log.finest("location returned " + location);
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * oesdatapoolintegration.pdp.ObligationParserType#parseObligations(java.util.Map)
	 */
	public String parseObligations(Map<String, Obligation> obligations) throws Exception {

		StringBuilder sb = new StringBuilder();

		if (obligations != null && obligations.size() > 0) {

			Collection<Obligation> collection = obligations.values();

			boolean firstObligation = true;
			for (Obligation obligation : collection) {

				// the obligations are joint using the AND operator
				if (!firstObligation) {
					sb.append(AND_STR);
				} else {
					firstObligation = false;
				}

				sb.append(LEFT_STR);

				Map<String, String> conditions = obligation.getStringValues();

				if (conditions != null && conditions.size() > 0) {

					boolean firstValue = true;
					boolean several = false;
					if (conditions.size() > 1) {
						several = true;
					}
					for (String condition : conditions.keySet()) {
						// the different attributes in the same obligation are
						// joint using the OR operator
						if (!firstValue) {
							sb.append(OR_STR);
						} else {
							firstValue = false;
						}

						// if the attribute name is POSITION, the
						// LocationResolver will be used for this attribute.
						if (condition.toUpperCase().equals(POSITION_STR)) {
							sb.append(LEFT_STR);
							String coordinates = getCoordinates(conditions.get(condition).toUpperCase());
							sb.append(WITHIN_STR.replace("$1", coordinates));
							sb.append(RIGHT_STR);

						} else {

							// otherwise, the statement is done with the equals
							// expression
							if (several) {
								sb.append(LEFT_STR);
							}
							sb.append(condition.toUpperCase());
							sb.append(EQUALS_STR);
							sb.append(conditions.get(condition).toUpperCase());
							if (several) {
								sb.append(RIGHT_STR);
							}
						}
					}

					sb.append(RIGHT_STR);

				} else {
					log.fine("No attributes in this obligation " + obligation.getObligationId());
				}
			}

		} else {

			log.finer("No obligations returned");

		}

		log.finest("query returned " + sb.toString());

		return sb.toString();
	}
}

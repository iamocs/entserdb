/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oracle.oes.datapoolintegration.pdp;

/**
 *
 * @author OCS
 */

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import oracle.security.jps.openaz.pep.PepRequestFactoryImpl;

import org.openliberty.openaz.azapi.pep.Obligation;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import weblogic.security.principal.WLSUserImpl;

import com.oracle.oes.datapoolintegration.auth.ConditionAuthResponse;
import com.oracle.oes.datapoolintegration.auth.FilteredDataAuthResponse;
import com.oracle.oes.datapoolintegration.auth.SimpleAuthResponse;
import com.oracle.oes.datapoolintegration.parser.ObligationParserFactory;
import com.oracle.oes.datapoolintegration.parser.ObligationParserType;

public class OESWrapper implements Serializable {

	// id
	private static final long serialVersionUID = -1888243127611101394L;

	// log
	private static Logger log = Logger.getLogger(OESWrapper.class.getName());

	// constants

	public static final String SIMPLE_RESOURCE_TYPE = "SimpleResourceType";

	public static final String OBLIGATION_RESOURCE_TYPE = "ConditionResourceType";

	public static final String FILTERED_DATA_RESOURCE_TYPE = "FilteredDataResourceType";

	private String application;

	private String operation;

	// constructor

	public String getApplication() {
		return application;
	}

	public String getOperation() {
		return operation;
	}

	public OESWrapper(String application, String operation) {
		super();
		this.application = application;
		this.operation = operation;
		log.finest("new instance of OESWrapper for application/operation = " + application + "/" + operation);
	}

	// public methods

	/**
	 * Performs a query to the OES and returns if the access is granted or not.
	 * 
	 * @param userId
	 *            User identificator
	 * @param action
	 *            Action to be accessed
	 * @param resourceString
	 *            Resource Path to be accessed, in the form:
	 *            application/resourceType/resourceName
	 * @return SimpleAuthResponse object, with true or false result
	 * @throws Exception
	 */
	public SimpleAuthResponse getSimpleAuthResponse(String userId, String action, String resourceString) throws Exception {

		log.finest("getSimpleAuthResponse(String userId, String action, String resourceString)");

		SimpleAuthResponse result = new SimpleAuthResponse();
		try {

			Subject s = getSubject(userId);
			HashMap<String, String> env = new HashMap<String, String>();
			if (getOperation() != null) {
				env.put("OPERATION", getOperation());
			}

			// composing the resource path
			String resourcePath = getApplication() + "/" + SIMPLE_RESOURCE_TYPE + "/" + resourceString;

			// asking the oessm about the access...
			PepRequestFactory pepRequestFactory = PepRequestFactoryImpl.getPepRequestFactory();
			PepRequest request = pepRequestFactory.newPepRequest(s, action, resourcePath, env);
			PepResponse response = request.decide();

			// getting results
			result.setAllowed(response.allowed());

		} catch (Exception e) {
			log.severe("getSimpleAuthResponse - exception while asking for access = " + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Performs a query to the OES and returns if the access is granted and the
	 * obligations returned.
	 * 
	 * @param userId
	 *            User identificator
	 * @param action
	 *            Action to be accessed
	 * @param resourceString
	 *            Resource Path to be accessed, in the form:
	 *            application/resourceType/resourceName
	 * @return ConditionAuthResponse object with the result of the access
	 *         (true/false) and the obligations returned
	 * @throws Exception
	 */
	public ConditionAuthResponse getConditionAuthResponse(String userId, String action, String resourceString) throws Exception {

		log.finest("getConditionAuthResponse(String userId, String action, String resourceString)");

		ConditionAuthResponse result = new ConditionAuthResponse();
		try {

			// composing the resource path
			String resourcePath = getApplication() + "/" + OBLIGATION_RESOURCE_TYPE + "/" + resourceString;

			Subject s = getSubject(userId);
			HashMap<String, String> env = new HashMap<String, String>();
			if (getOperation() != null) {
				env.put("OPERATION", getOperation());
			}

			// asking the oessm about the access...
			PepRequestFactory pepRequestFactory = PepRequestFactoryImpl.getPepRequestFactory();
			PepRequest request = pepRequestFactory.newPepRequest(s, action, resourcePath, env);
			PepResponse response = request.decide();

			// getting the results
			result.setAllowed(response.allowed());

			// if obligations are returned, they are processed using the
			// ObligationParserFactory
			if (response.getObligations() != null) {

				Map<String, Obligation> obligations = response.getObligations();
				ObligationParserType obligationParser = ObligationParserFactory.getInstance();

				// the obligations are translated into a string, which is put
				// into the returned object
				String translation = obligationParser.parseObligations(obligations);
				result.setCondition(translation);

			} else {
				log.severe("getConditionAuthResponse - no conditions returned");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.severe("getConditionAuthResponse - exception while asking for access = " + e.getMessage());
		}

		return result;
	}

	/**
	 * Performs a filter on a list of resources based on queries to the OES.
	 * 
	 * @param userId
	 *            User identificator
	 * @param action
	 *            Action to be accessed
	 * @param resourceString
	 *            Resource Path to be accessed, in the form:
	 *            application/resourceType/resourceName
	 * @param doc
	 *            XML Document to be filtered
	 * @return FilteredDataAuthResponse object with the result of the access and
	 *         the XML Document filtered with the obligations returned.
	 * @throws Exception
	 */
	public FilteredDataAuthResponse getFilteredDataAuthResponse(String userId, String action, String resourceString, Document doc) throws Exception {

		log.finest("getFilteredDataAuthResponse(String userId, String action, String resourceString, Document doc)");

		FilteredDataAuthResponse result = new FilteredDataAuthResponse();
		try {

			// composing the resource path
			String resourcePath = getApplication() + "/" + FILTERED_DATA_RESOURCE_TYPE + "/" + resourceString;

			Subject s = getSubject(userId);
			HashMap<String, String> env = new HashMap<String, String>();
			if (getOperation() != null) {
				env.put("OPERATION", getOperation());
			}

			// asking the oessm about the access...
			PepRequestFactory pepRequestFactory = PepRequestFactoryImpl.getPepRequestFactory();
			PepRequest request = pepRequestFactory.newPepRequest(s, action, resourcePath, env);
			PepResponse response = request.decide();

			// getting the results
			result.setAllowed(response.allowed());

			List<String> expressions = new ArrayList<String>();

			if (response.getObligations() != null) {
				Map<String, Obligation> obligations = response.getObligations();
				Collection<Obligation> collection = obligations.values();
				for (Obligation obligation : collection) {
					Collection<String> collectionValues = obligation.getStringValues().values();
					for (String collectionValue : collectionValues) {
						expressions.add(collectionValue);
						log.finer("getFilteredDataAuthResponse - obligation value = " + collectionValue);
					}
				}
			} else {
				log.fine("getFilteredDataAuthResponse - no conditions returned");
			}

			Document filteredDocument = applyXPathFilter(expressions, doc);
			result.setObject(filteredDocument);

		} catch (Exception e) {
			log.severe("getFilteredDataAuthResponse - exception while asking for access = " + e.getMessage());
		}

		return result;
	}

	// private methods

	/**
	 * Apply a list of XPath expressions to a XML document
	 * 
	 * @param expressions
	 *            List of expressions to be applied
	 * @param doc
	 *            XML Document to be filtered
	 * @return
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static Document applyXPathFilter(List<String> expressions, Document doc) throws XPathExpressionException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, SAXException, IOException {

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		Document newXmlDocument = null;

		// the root document is a copy of the one received
		newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = newXmlDocument.createElementNS(doc.getDocumentElement().getNamespaceURI(), doc.getDocumentElement().getLocalName());
		newXmlDocument.appendChild(root);

		// the expressions are run over the children of the root element
		for (String exprStr : expressions) {
			log.finest("applyXPathFilter - applying expression = " + exprStr);
			XPathExpression expr = xpath.compile(exprStr);
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			// the nodes returned are put into the root element
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				Node copyNode = newXmlDocument.importNode(node, true);
				root.appendChild(copyNode);
			}

		}

		return newXmlDocument;
	}

	/**
	 * Method for creating the Subject object from the userName.
	 * 
	 * @param userName
	 *            Identification of the user
	 * @return Subject object with the Principal bound to the user
	 * @throws Exception
	 */
	private Subject getSubject(String userName) {

		Subject subject = new Subject();
		Set<Principal> principalsForSubject = new HashSet<Principal>();
		principalsForSubject.add(new WLSUserImpl(userName));
		subject.getPrincipals().addAll(principalsForSubject);

		return subject;
	}

}

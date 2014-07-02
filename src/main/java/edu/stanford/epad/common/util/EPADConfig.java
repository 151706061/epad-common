/*
 * Copyright 2012 The Board of Trustees of the Leland Stanford Junior University.
 * Author: Daniel Rubin, Alan Snyder, Debra Willrett. All rights reserved. Possession
 * or use of this program is subject to the terms and conditions of the Academic
 * Software License Agreement available at:
 *   http://epad.stanford.edu/license/
 */
package edu.stanford.epad.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;

/**
 * Read the "etc/proxy-config.properties" file on start-up and provides method to look up values.
 */
public class EPADConfig
{
	public static final String serverProxy = EPADConfig.getInstance().getStringPropertyValue("serverProxy");
	public static final String baseAnnotationDir = EPADConfig.getInstance().getStringPropertyValue("baseAnnotationDir");
	public static final String dicomServerPort = EPADConfig.getInstance().getStringPropertyValue("DicomServerPort");
	public static final String aeTitle = EPADConfig.getInstance().getStringPropertyValue("DicomServerAETitle");
	public static final String eXistUsername = EPADConfig.getInstance().getStringPropertyValue("username");
	public static final String eXistPassword = EPADConfig.getInstance().getStringPropertyValue("password");
	public static final String epadDatabaseUsername = EPADConfig.getInstance().getStringPropertyValue(
			"epadDatabaseUsername");
	public static final String epadDatabasePassword = EPADConfig.getInstance().getStringPropertyValue(
			"epadDatabasePassword");
	public static final String epadDatabaseURL = EPADConfig.getInstance().getStringPropertyValue("epadDatabaseURL");
	public static final String dcm4CheeDatabaseUsername = EPADConfig.getInstance().getStringPropertyValue(
			"dcm4CheeDatabaseUsername");
	public static final String dcm4CheeDatabasePassword = EPADConfig.getInstance().getStringPropertyValue(
			"dcm4CheeDatabasePassword");
	public static final String dcm4CheeDatabaseURL = EPADConfig.getInstance().getStringPropertyValue(
			"dcm4CheeDatabaseURL");
	public static final String eXistCollection = EPADConfig.getInstance().getStringPropertyValue("collection");
	public static final String aim3Namespace = EPADConfig.getInstance().getStringPropertyValue("namespace");
	public static final String eXistURI = EPADConfig.getInstance().getStringPropertyValue("serverUrlUpload");
	public static final String eventResourceURI = EPADConfig.getInstance().getStringPropertyValue("eventResourceURI");
	public static final String seriesOrderURI = EPADConfig.getInstance().getStringPropertyValue("seriesOrderURI");

	private static final EPADLogger log = EPADLogger.getInstance();
	private static final EPADConfig ourInstance = new EPADConfig();

	private Properties properties;

	public static EPADConfig getInstance()
	{
		return ourInstance;
	}

	/**
	 * Get the base directory for the ePAD web server.
	 * <p>
	 * For the moment, we use ~/DicomProxy as the base directory for compatibility with older calls. Ultimately we will
	 * change location to something more general.
	 * 
	 * @return File Base directory for ePAD web server.
	 */
	public static String getEPADWebServerBaseDir()
	{
		return System.getProperty("user.home") + "/DicomProxy/";
	}

	public static String getEPADWebServerWebappsDir()
	{
		return getEPADWebServerBaseDir() + "webapps/";
	}

	public static String getEPADWebServerResourcesDir()
	{
		return getEPADWebServerBaseDir() + "resources/";
	}

	public static String getEPADWebServerEtcDir()
	{
		return getEPADWebServerBaseDir() + "etc/";
	}

	public static String getEPADWebServerDICOMScriptsDir()
	{
		return getEPADWebServerEtcDir() + "scripts/";
	}

	public static String getEPADWebServerDICOMBinDir()
	{
		return getEPADWebServerDICOMScriptsDir() + "tpl/bin/";
	}

	public static String getEPADWebServerIconsDir()
	{
		return getEPADWebServerEtcDir() + "icons/";
	}

	public static String getEPADWebServerMyScriptsDir()
	{
		return getEPADWebServerEtcDir() + "scripts/myscripts/bin/";
	}

	public static String getEPADWebServerMySQLScriptDir()
	{
		return getEPADWebServerEtcDir() + "db/mysql/";
	}

	public static String getEPADWebServerLoginDir()
	{
		return getEPADWebServerEtcDir() + "login/";
	}

	public static String getEPADWebServerUploadDir()
	{
		return getEPADWebServerResourcesDir() + "upload/";
	}

	public static String getEPADWebServerRSNADir()
	{
		return getEPADWebServerResourcesDir() + "rsna/";
	}

	public static String getEPADWebServerAnnotationsDir()
	{
		return getEPADWebServerResourcesDir() + "annotations/";
	}

	public static String getEPADWebServerAnnotationsUploadDir()
	{
		return getEPADWebServerAnnotationsDir() + "upload/";
	}

	public static String getEPADWebServerSchemaDir()
	{
		return getEPADWebServerResourcesDir() + "schema/";
	}

	public static String getEPADWebServerAIM3XSDFilePath()
	{
		return getEPADWebServerSchemaDir() + "AIM_v3.xsd";
	}

	public static String getEPADWebServerLogDir()
	{
		return getEPADWebServerBaseDir() + "log/";
	}

	public static String getEPADWebServerPNGDir()
	{
		return getEPADWebServerResourcesDir() + "dicom/";
	}

	public static String getEPADWebServerDicomTagDir()
	{
		return getEPADWebServerResourcesDir() + "dicom/";
	}

	public static String getEPADWebServerDicomDir()
	{
		return getEPADWebServerResourcesDir() + "dicom/";
	}

	public static String getEPADWebServerPNGGridDir()
	{
		return getEPADWebServerResourcesDir() + "dicom_grid/";
	}

	public static String getEPADWebServerPluginConfigFilePath()
	{
		return getEPADWebServerEtcDir() + "plugin-config.txt";
	}

	public static String getEPADWebServerConfigFilePath()
	{
		return getEPADWebServerEtcDir() + "proxy-config.properties";
	}

	public static String getEPADWebServerLogFilePath()
	{
		return getEPADWebServerLogDir() + "dicom-proxy.log";
	}

	public static String getEPADWebServerJettyConfigFilePath()
	{
		return getEPADWebServerEtcDir() + "jetty-config.xml";
	}

	private EPADConfig()
	{
		try {
			properties = new Properties();
			File configFile = getConfigurationFile();
			if (!configFile.exists())
				throw new IllegalStateException("Could not find ePAD configuration file " + configFile.getAbsolutePath());

			FileInputStream fis = new FileInputStream(configFile);
			try {
				properties.load(fis);
				log.info("Using ePAD configuration file " + configFile.getAbsolutePath());
			} finally {
				IOUtils.closeQuietly(fis);
			}
		} catch (Exception e) {
			log.severe("Error reading ePAD configuration file", e);
		}
	}

	private File getConfigurationFile()
	{
		File configFile = new File(EPADConfig.getEPADWebServerConfigFilePath());
		log.info("Found configuration file " + configFile.getAbsolutePath());

		return configFile;
	}

	/**
	 * Returns the values of a property in ePAD's configuration file.
	 * 
	 * @param name key in config file.
	 * @return the value of that parameter.
	 */
	public String getParam(String name)
	{
		return properties.getProperty(name);
	}

	/**
	 * Returns the value of a parameter in proxy-config.properties as an integer.
	 * 
	 * @param name key
	 * @return the parameter value as an integer
	 * @throws IllegalArgumentException if the value is not an integer in the config file.
	 */
	public int getIntParam(String name)
	{
		String s = properties.getProperty(name);
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("The " + name + " parameter value needs to be an integer. Its value was " + s);
		}
	}

	/**
	 * 
	 * @param propertyName
	 * @return
	 * @throws IllegalArgumentException
	 */
	public String getStringPropertyValue(String propertyName)
	{
		String parameterValue = getParam(propertyName);

		if (parameterValue == null || parameterValue.length() == 0) {
			String errorMessage = "No value for parameter " + propertyName + " in configuration file";
			log.warning(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		return parameterValue;
	}

	/**
	 * 
	 * @param propertyName
	 * @return
	 * @throws IllegalArgumentException
	 */
	public int getIntegerPropertyValue(String propertyName)
	{
		String parameterValue = getStringPropertyValue(propertyName);
		try {
			return Integer.parseInt(parameterValue);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("The parameter value for property " + propertyName
					+ " needs to be an integer. It value was " + parameterValue);
		}
	}

	/**
	 * Get all the key,values as a Map of Strings.
	 * 
	 * @return Map of String keys to String values
	 */
	public Map<String, String> getAllPropertyValues()
	{
		Set<String> keys = properties.stringPropertyNames();
		Map<String, String> retVal = new HashMap<String, String>();

		for (String key : keys) {
			retVal.put(key, properties.getProperty(key));
		}
		return retVal;
	}
}

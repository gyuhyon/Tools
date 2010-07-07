/*******************************************************************************
 * Copyright (c) 2010 Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the new BSD license
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/bsd-license.html
 * 
 * Contributors:
 *     Christopher Haines, Dale Scheppler, Nicholas Skaggs, Stephen V. Williams - initial API and implementation
 ******************************************************************************/
package org.vivoweb.ingest.fetch;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vivoweb.ingest.util.RecordHandler;
import org.vivoweb.ingest.util.Task;
import org.vivoweb.ingest.util.XMLRecordOutputStream;
import org.xml.sax.SAXException;
import ORG.oclc.oai.harvester2.app.RawWrite;

/**
 * Class for harvesting from OAI Data Sources
 * @author Dale Scheppler
 * @author Chris Haines
 *
 */
public class OAIHarvest extends Task {
	private static Log log = LogFactory.getLog(OAIHarvest.class);
	/**
	 * A listing of the paramaters that need to be in the configuration file for
	 * the OAI harvest to function properly.
	 * @author Dale Scheppler
	 * @author Chris Haines
	 */
	protected static final String[] arrRequiredParamaters = {"address", "startDate", "endDate", "filename"};
	/**
	 * The website address of the OAI Repository without the protocol prefix (No http://)
	 * @author Dale Scheppler
	 */
	private String strAddress;
	/**
	 * The start date for the range of records to pull, format is YYYY-MM-DD<br>
	 * If time is required, format is YYYY-MM-DDTHH:MM:SS:MSZ<br>
	 * Some repositories do not support millisend resolution.<br>
	 * Example 2010-01-15T13:45:12:50Z<br>
	 * @author Dale Scheppler
	 */
	private String strStartDate;
	/**
	 * The end date for the range of records to pull, format is YYYY-MM-DD<br>
	 * If time is required, format is YYYY-MM-DDTHH:MM:SS:MSZ<br>
	 * Some repositories do not support millisend resolution.<br>
	 * Example 2010-01-15T13:45:12:50Z<br>
	 * @author Dale Scheppler
	 */
	private String strEndDate;
	/**
	 * The output stream to send the harvested XML to. Can be any type of output stream. Currently using a fileoutputstream.
	 * @author Dale Scheppler
	 */
	private OutputStream osOutStream;

	/**
	 * Calls the RawWrite function of the OAI Harvester example code. Writes to a file output stream.<br>
	 * Some repositories are configured incorrectly and this process will not work. For those a custom<br>
	 * method will need to be written.
	 * @author Dale Scheppler
	 * @param strAddress - The website address of the repository, without http://
	 * @param strStartDate - The date at which to begin fetching records, format and time resolution depends on repository.
	 * @param strEndDate - The date at which to stop fetching records, format and time resolution depends on repository.
	 * @param fosOutStream - The file output stream to write to.
	 * @throws Exception General exception catch if no other exceptions caught.
	 * @throws SAXException Thrown if there is an error in SAX.
	 * @throws TransformerException Thrown if there is an error during XML transform.
	 * @throws NoSuchFieldException Thrown if one of the fields queried does not exist.
	 */
	public static void execute(String strAddress, String strStartDate, String strEndDate, OutputStream osOutStream) throws Exception, SAXException, TransformerException, NoSuchFieldException
	{
		RawWrite.run("http://" + strAddress, strStartDate, strEndDate, "oai_dc", "", osOutStream);
	}
	
	@Override
	//FIXME CAH this needs documentation
	protected void acceptParams(Map<String, String> params) throws ParserConfigurationException, SAXException, IOException {
		this.strAddress = getParam(params, "address", true);
		this.strStartDate = getParam(params, "startDate", true);
		this.strEndDate = getParam(params, "endDate", true);
		String repositoryConfig = getParam(params, "repositoryConfig", true);
		String recordTag = getParam(params, "recordTag", true);
		String xmlHead = getParam(params, "xmlHead", true);
		String xmlFoot = getParam(params, "xmlFoot", true);
		String idRegex = getParam(params, "idRegex", true);
		RecordHandler rhRecordHandler = RecordHandler.parseConfig(repositoryConfig);
		rhRecordHandler.setOverwriteDefault(true);
		this.osOutStream = new XMLRecordOutputStream(recordTag, xmlHead, xmlFoot, idRegex, rhRecordHandler);
	}
	
	@Override
	//FIXME CAH this needs documentation
	protected void runTask() throws NumberFormatException {
		try {
			System.out.println("http://" + this.strAddress);
			System.out.println(this.strStartDate);
			System.out.println(this.strEndDate);
			RawWrite.run("http://" + this.strAddress, this.strStartDate, this.strEndDate, "oai_dc", "", osOutStream);
		} catch(IOException e) {
			log.error(e.getMessage(),e);
		} catch(ParserConfigurationException e) {
			log.error(e.getMessage(),e);
		} catch(SAXException e) {
			log.error(e.getMessage(),e);
		} catch(TransformerException e) {
			log.error(e.getMessage(),e);
		} catch(NoSuchFieldException e) {
			log.error(e.getMessage(),e);
		}
	}

}

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
package org.vivoweb.ingest.translate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.vivoweb.ingest.util.Record;
import org.vivoweb.ingest.util.RecordHandler;

import com.hp.gloze.Gloze;
import com.hp.hpl.jena.rdf.model.*;
import org.vivoweb.ingest.translate.Translator;

/**
 * Gloze Tranlator
 * This class translates XML into its own natrual RDF ontology
 * using the gloze library.  Translation into the VIVO ontology
 * is completed using the RDF Translator.
 * 
 * TODO:  Identify additional parameters required for translation
 * TODO:  Identify methods to invoke in the gloze library
 * 
 * @author Stephen V. Williams swilliams@ctrip.ufl.edu
 */
public class GlozeTranslator extends Translator{

	/**
	 * 
	 */
	//FIXME remove this and use the incoming stream if possible
	protected File incomingXMLFile;				
	protected File incomingSchema;
	protected URI uriBase;

	/**
	 * @param xmlFile the file to translate
	 */
	public void setIncomingXMLFile(File xmlFile){
		this.incomingXMLFile = xmlFile;
	}
	
	/**
	 * 
	 * @param schema the schema that gloze can use, but doesn't need to translate the xml
	 */
	public void setIncomingSchema(File schema){
		this.incomingSchema = schema;
	}
	
	/**
	 * 
	 * @param base the base uri to apply to all relative entities
	 */
	public void setURIBase(String base){
		try {
			this.uriBase = new URI(base);
		} catch (URISyntaxException e) {
			log.error("",e);
		}
	}
	
	/**
	 * 
	 */
	public GlozeTranslator() {
		this.setURIBase("http://vivoweb.org/glozeTranslation/noURI/");
	}
	
	
	public void translateFile(){
		Gloze gl = new Gloze();
		
		Model outputModel = ModelFactory.createDefaultModel();
		
		try {
			//Create a temporary file to use for translation
			File tempFile = new File("temp");
			//FileOuputStream tempWrite = new FileOutputStream(tempFile);
			
			
			
			
			gl.xml_to_rdf(incomingXMLFile, new File("test"), this.uriBase , outputModel);
		} catch (Exception e) {
			log.error("",e);
		}
		
		outputModel.write(this.outStream);
	}
	
	/**
	 * 
	 */
	public void execute() {
		if (this.uriBase != null && this.inStream != null ){	
			log.info("Translation: Start");
			
			translateFile();
			
			log.info("Translation: End");
		}
		else {
			log.error("Invalid Arguments: Gloze Translation requires a URIBase and XMLFile");
			throw new IllegalArgumentException();
		}
	}
	
	/***
	 * 
	 * @param switch states if you are using the file methods or the record handler
	 * @param inFile the xml file to translate
	 * @param inRecordHandler
	 * @param schema
	 * @param outFile
	 * @param outRecordHandler
	 * @param uriBase - required for gloze translation the unset URIBASE used is http://vivoweb.org/glozeTranslation/noURI/
	 */
	public void parseArgsExecute(String[] args){
		if (args.length != 5) {
			  log.error("Invalid Arguments: GlozeTranslate requires 5 arguments.  The system was supplied with " + args.length);
		}
		else {
			
			//File Translation
			if (args[0].equals("-f")) {
				this.setInStream(new FileInputStream(new File(args[1])));
				if (!args[3].equals("") && args[3] != null){
					this.setOutStream(new FileOutputStream(new File(args[3])));
				} else {
					this.setOutStream(System.out);
				}
				
				//the schema is not required but aids in xml translation
				if (!args[2].equals("") && args[2] != null){
					this.setIncomingXMLFile(new File(args[2]));
				}
				
				//the uri base if not set is http://vivoweb.org/glozeTranslation/noURI/"
				if (args[4].equals("") && args[4] != null) {					
					this.setURIBase(args[4]);
				} 
				
				this.execute();
			}
			else if (args[0].equals("-rh")) {
				try {
					//the uri base if not set is http://vivoweb.org/glozeTranslation/noURI/"
					if (args[4].equals("") && args[4] != null) {					
						this.setURIBase(args[4]);
					} 
				
					//pull in the translation xsl
					if (!args[2].equals("") && args[2] != null){
						glTrans.setIncomingSchema(new File(args[2]));
					}	
					
					//create record handlers
					RecordHandler inStore = RecordHandler.parseConfig(args[1]);
					if (!args[3].equals("") && args[3] != null){
						RecordHandler outStore = RecordHandler.parseConfig(args[3]);
					} else {
						throw new IllegalArgumentException("Record Handler Execution requires and out bound record handler");
					}
					
					//create a output stream for writing to the out store
					ByteArrayOutputStream buff = new ByteArrayOutputStream();
					
					// get from the in record and translate
					for(Record r : inStore){
						this.setInStream(new ByteArrayInputStream(r.getData().getBytes()));
						this.setOutStream(buff);
						this.execute();
						buff.flush();
						outStore.addRecord(r.getID(),buff.toString());
						buff.reset();
					}
				
					buff.close();
				}
				catch (Exception e) {
					log.error("",e);
				}
			}
			else {
				log.error("Invalid Arguments: Translate option " + args[0] + " not handled.");
			}		
		}
	}
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GlozeTranslator glTrans = new GlozeTranslator();
		glTrans.parseArgsExecute(args);		
	}
}

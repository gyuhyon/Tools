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
package org.vivoweb.ingest.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

/**
 * @author Christopher Haines (hainesc@ctrip.ufl.edu)
 *
 */
public class GenTextFileRecordHandler extends RecordHandler {

	protected static Log log = LogFactory.getLog(GenTextFileRecordHandler.class);
//	private String directory;
	protected FileObject fileDirObj;
	
	/**
	 * Constructor
	 * @param fileDir 
	 * @throws IOException 
	 * 
	 */
	public GenTextFileRecordHandler(String fileDir) throws IOException {
		FileSystemManager fsMan = VFS.getManager();
//		this.directory = fileDir;
		this.fileDirObj = fsMan.resolveFile(new File("."),fileDir);
		if(!this.fileDirObj.exists()) {
			log.info("Directory '"+fileDir+"' Does Not Exist, attempting to create");
			this.fileDirObj.createFolder();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#addRecord(org.vivoweb.ingest.util.Record)
	 */
	@Override
	public void addRecord(Record rec) throws IOException {
		FileObject fo = this.fileDirObj.resolveFile(rec.getID());
		if(fo.exists()) {
			throw new IOException("Failed to add record "+rec.getID()+" because file "+fo.getName().getFriendlyURI()+" already exists.");
		}
		fo.createFile();
		if(!fo.isWriteable()) {
			throw new IOException("Insufficient file system privileges to add record "+rec.getID()+" to file "+fo.getName().getFriendlyURI());
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fo.getContent().getOutputStream()));
		bw.append(rec.getData());
		bw.close();
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#delRecord(java.lang.String)
	 */
	@Override
	public void delRecord(String recID) throws IOException {
		FileObject fo = this.fileDirObj.resolveFile(recID);
		if(!fo.exists()) {
			log.warn("Attempted to delete record "+recID+", but file "+fo.getName().getFriendlyURI()+" did not exist.");
		} else if(!fo.isWriteable()) {
			throw new IOException("Insufficient file system privileges to delete record "+recID+" from file "+fo.getName().getFriendlyURI());
		} else if(!fo.delete()) {
			throw new IOException("Failed to delete record "+recID+" from file "+fo.getName().getFriendlyURI());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.vivoweb.ingest.util.RecordHandler#getRecordData(java.lang.String)
	 */
	@Override
	public String getRecordData(String recID) throws IllegalArgumentException, IOException {
		StringBuilder sb = new StringBuilder();
		FileObject fo = this.fileDirObj.resolveFile(recID);
		BufferedReader br = new BufferedReader(new InputStreamReader(fo.getContent().getInputStream()));
		String line;
		while((line = br.readLine()) != null){
			sb.append(line);
			sb.append("\n");
		}
		br.close();
//		} catch(FileNotFoundException e) {
//			log.error("Record File Not Found For "+recID);
//			throw new IllegalArgumentException("Record File Not Found For "+recID);
//		} catch(IOException e) {
//			log.error("Error Reading Contents of file '"+fo.getName().getFriendlyURI()+"'");
//			throw e;
//		}
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	@SuppressWarnings("synthetic-access")
	public Iterator<Record> iterator() {
		return new TextFileRecordIterator();
	}
	
	private class TextFileRecordIterator implements Iterator<Record> {
		Iterator<FileObject> fileIter;
		
		private TextFileRecordIterator() {
			LinkedList<FileObject> fileListing = new LinkedList<FileObject>();
			try {
				for(FileObject file : GenTextFileRecordHandler.this.fileDirObj.getChildren()) {
					if(!file.isHidden()) {
						fileListing.add(file);
//						System.out.println(file.getName().getBaseName());
					}
				}
			} catch(FileSystemException e) {
				log.error("",e);
			}
			this.fileIter = fileListing.iterator();
		}
		
		public boolean hasNext() {
			return this.fileIter.hasNext();
		}
		
		public Record next() {
			try {
				Record result = getRecord(this.fileIter.next().getName().getBaseName());
				return result;
			} catch(IOException e) {
				throw new NoSuchElementException(e.getMessage());
			}
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
//	private String getPath(String recID) {
//		return getDirectory()+"/"+recID;
//	}
	
}

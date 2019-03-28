package br.com.tlmgroup.jkp.process;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.tlmgroup.commons.client.utils.UtilsClient;
import br.com.tlmgroup.jkp.bean.JKPDataImport;
import br.com.tlmgroup.jkp.bean.JKPFileStatus;
import br.com.tlmgroup.jkp.bean.ImportResponse;
import br.com.tlmgroup.jkp.dao.ImportDAO;
import br.com.tlmgroup.jkp.helper.AbstractUniversal;
import br.com.tlmgroup.jkp.helper.JKPCsvReader;
import br.com.tlmgroup.jkp.helper.Filehandler;

public class Import extends AbstractUniversal{
	
	UtilsClient utilsClient = new UtilsClient();
	
	public static final String SEND_IMPORT_ERROR_NOTIFICATIONS	= "y";
	public static final String IMPORT_FILES_FROM_FTP 	 		= "y";
	public static final String DELETE_FILES_FTP					= "y";
	public static final String NO_DELETE_FILES_FTP				= "n";

	/**
	 * Process the JKP file import data to tables:
	 * - jkp_file
	 * - jkp_status
	 * - consumption
	 * @param filename
	 * @return 
	 * @throws SQLException
	 */
	public JKPFileStatus addToDB(String filename) throws SQLException{
		JKPFileStatus jkpStatusReturn = null; 
		JKPCsvReader jkpfile = new JKPCsvReader(filename);
		JKPDataImport jkpData = null;
		ImportDAO dao = new ImportDAO();
		dao.openConnection();
		Integer jkpId = null;
		try{
			//Verify if file was already imported.
			//If so, jump to next step of process
			 jkpId = dao.jkpIdImported(jkpfile.getJkpFileIdentification());
			 
			 if(jkpId == null){
				 jkpfile.open();
				 
				 //Inserts file into jkp_file table, and gets the last inserted ID
				 jkpId = dao.insertJKPFile(jkpfile.getJkpFileIdentification());
				 try{
					 log.info(">>> Batch consumption read");
					 while(true){
						 jkpData = jkpfile.read();
						 if(jkpData != null){
							 //add each line of file into DB
							 dao.jkpAddConsumption(jkpId, jkpData);							
						 }else{
							 log.info("<<< Batch consumption read");
							 //commits the transaction
							 dao.jkpCommitConsumption();
							 break;
						 }
					 }
					 //Inserts success state into Status table
					 jkpStatusReturn = dao.jkpInsertStatusSuccess(jkpId, jkpfile.getStatus(), "File inserted with success");
				 }catch(Exception e){
					 //Rollbacks transaction and insert Fail into status table
					 log.error("<<< addToDB - Import Error:" + e.getMessage()); 
					 dao.jkpRollback();					 
					 JKPFileStatus jkpStatus = jkpfile.getStatus();
					 jkpStatusReturn = dao.jkpInsertStatusFailed(jkpId, jkpStatus, "Inserting error on line: " + jkpStatus.getLines());
				 }
				 jkpfile.close();
	
			 }else{
				 jkpStatusReturn = dao.jkpInsertStatusFailed(jkpId, jkpfile.getStatus(), "File already imported before, ignoring file");			 
			 }	
		 }catch(Exception e){
			 log.error("<<< addToDB -  Error while inserting,  line:" + jkpfile.getActualLine());
			 jkpStatusReturn  = dao.jkpInsertStatusFailed(jkpId, jkpfile.getStatus(), "Error: " + e.getMessage());
		 }
		 dao.close();
		return jkpStatusReturn;
	}
	
	/**
	 * Import a list of JkpFiles, imports to database or rejects each file 
	 * @param listFiles
	 * @param avoidImportNotification, if receive y not send notification
	 * @return List<JKPStatus> with IMPORTED/FAILED status
	 */
	public List<JKPFileStatus> processFiles(List<File> listFiles, String sendNotification){
		Filehandler fhandler = new Filehandler(); 
		List<JKPFileStatus> jkpStatusList = new ArrayList<>();
		listFiles.forEach( file->{
			if(file.canRead()){
				log.info("Processing File: " + file.getAbsolutePath());
				try{
					JKPFileStatus jkpStatus = this.addToDB(file.getAbsolutePath());
					
					if(jkpStatus.getStatus().equals(JKPFileStatus.IMPORTED)){
						fhandler.moveToImported(file);
					}else{						
						fhandler.moveToRejected(file);

						if(sendNotification.equals(Import.SEND_IMPORT_ERROR_NOTIFICATIONS)){
							this.notficateImportError(jkpStatus);
						}else{
							log.info("Avoiding Send Import Error Notifications");
						}
						
						log.error("Unable to importing File: " + jkpStatus.toString());
					}
					jkpStatusList.add(jkpStatus);
				}catch(SQLException e){
					log.error("Unable to add " + file.getAbsolutePath() + " to database", e);
				}
			}else{
				log.error("Unable to read File: " + file.getAbsolutePath());
			}
		});
		return jkpStatusList;
	}
	
	private Map<String,String> jkpImportError(JKPFileStatus jkpStatus){
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    Date date = new Date();  
	    String importDate = formatter.format(date);
		
		Map<String,String> wildcards = new HashMap<>();
		/*
		 * Wildcards registered in notification template jkp_import_failed on Nid
		 */
		wildcards.put("JKP_FILE", jkpStatus.getFilename());   
		wildcards.put("JKP_file_line_number", jkpStatus.getLines().toString());
		wildcards.put("JKP_file_size", jkpStatus.getSizeFriendlyFormat());
		wildcards.put("JKP_file_import_date", importDate);
		wildcards.put("JKP_file_md5sum", jkpStatus.getMd5());
		
		return wildcards;
	}
	
	private String notficateImportError(JKPFileStatus jkpStatus){
		String notificationReturn = ""; 
		try {
			
			//Template registered on Nid (BA) to send JKP import errors
			notificationReturn = utilsClient.sendNotification(cfg().notifImportFailedTemplate(), cfg().notifImportFailedSub(), cfg().notifImportFailedUser(), this.jkpImportError(jkpStatus));
			
		} catch (Exception e) {
			notificationReturn = "Unable to sendNotification, Template: " + cfg().notifImportFailedTemplate() + jkpStatus.toString();
			log.error(notificationReturn, e);
		}
		return notificationReturn;
	}
	
	
	public ImportResponse importProcess(String importFromFtp, String deleteFtpFiles, String sendNotification) throws Exception{
		log.info(String.format(">>> Starting importProcess parameter: importFromFtp=%s, deleteFtpFiles=%s, sendNotification=%s", importFromFtp, deleteFtpFiles, sendNotification));
		
		ImportResponse importResponse = new ImportResponse(importFromFtp, deleteFtpFiles, sendNotification);
		
		Filehandler fhandler = new Filehandler();
		String returnMessage = "";
		
		
		if(importFromFtp.equals(Import.IMPORT_FILES_FROM_FTP)){
			String importFtpResponse = "";
			log.info("Connecting to FTP: " + cfg().serverUsername() + "@" + cfg().serverAddress());
			try {				
				importFtpResponse = utilsClient.ImportFromFtp(cfg().keepassTitle(), cfg().localPath(), cfg().remotePath(), Filehandler.JKP_FILENAME_PATTERN, deleteFtpFiles, cfg().maxFilesDownload());
			} catch (Exception e) {
				log.error("Error on Import from FTP" + cfg().serverUsername() + "@" + cfg().serverAddress(), e);
				throw e;
			}
			log.info("FTP response" + importFtpResponse);
			importResponse.setFtpResponse(importFtpResponse);
		}else{
			String avoidFtpMessage = "Avoiding FTP Server, only JKP files on: " + cfg().localPath() + " will be processed"; 
			importResponse.setImportFromFtp(avoidFtpMessage);
			log.info(avoidFtpMessage);
		}
		
		if(!sendNotification.equals(Import.SEND_IMPORT_ERROR_NOTIFICATIONS)){
			importResponse.setSendImportErrorNotification("No error notifications will be send");
		}
		
	
		List<File> listFiles = fhandler.listJkpFiles(cfg().localPath());
		List<JKPFileStatus> jkpFileStatusList = new ArrayList<>();
		
		if(!listFiles.isEmpty()){
			jkpFileStatusList = this.processFiles(listFiles, sendNotification);
			returnMessage = listFiles.size() + " JKP files processed";
			log.info(returnMessage);
			importResponse.setJkpFileStatusList(jkpFileStatusList);
			importResponse.setImportMessage(returnMessage);
		}else{
			returnMessage = "Avoiding JKP import process, no JKP files to process in: " + cfg().localPath();
			log.info(returnMessage);
			importResponse.setImportMessage(returnMessage);
		}
	
		return importResponse;
	}
	
}

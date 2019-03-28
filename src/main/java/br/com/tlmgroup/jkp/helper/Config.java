package br.com.tlmgroup.jkp.helper;

import br.com.tlmgroup.environment.EnvironmentProperties;
import de.slackspace.openkeepass.domain.Entry;

public class Config {
	
	/**
	 * Keepass Entry Title - tag
	 */
	public static final String KEEPASSTILE  	= "JKP_IMPORT";
	
	/**
	 * Path to Jkp files - tags
	 */
	public static final String REMOTE_PATH   	= "REMOTEPATH";
	public static final String LOCAL_PATH    	= "LOCALPATH";
	
	/**
	 * FTP Credentials - tags
	 */
	public static final String SERVER_ADDRESS	= "SERVER_ADDRESS";
	public static final String SERVER_USERNAME	= "SERVER_USERNAME";
	public static final String SERVER_PASSWORD	= "SERVER_PASSWORD";
	public static final Integer MAX_FILES_DOWNLOAD	= 500;
	
	/**
	 * Templates tags to send Errors notifications on Nid 
	 */
	public static final String NOTIF_IMPORT_FAILED_TPL = "NOTIF_IMPORT_FAILED_TPL";
	public static final String NOTIF_EXPORT_FAILED_TPL = "NOTIF_EXPORT_FAILED_TPL";
	
	
	/**
	 * Subscription/User Ids to notificate errors in Export/Import JKP files - tags
	 */
	public static final String NOTIF_EXPORT_FAILED_SUB = "NOTIF_EXPORT_FAILED_SUB";
	public static final String NOTIF_EXPORT_FAILED_USR = "NOTIF_EXPORT_FAILED_USR";
	public static final String NOTIF_IMPORT_FAILED_SUB = "NOTIF_IMPORT_FAILED_SUB";
	public static final String NOTIF_IMPORT_FAILED_USR = "NOTIF_IMPORT_FAILED_USR";
	
	protected Entry keepassEnv = EnvironmentProperties.getInstance().getConfigurationEntry(KEEPASSTILE);

	
	/**
	 * Path where local JKP files will be stored
	 * Example: /tmp/jkp
	 * Example Windows: C:\jkp
	 * @return
	 */
	public String localPath(){
		return keepassEnv.getPropertyByName(LOCAL_PATH).getValue();
	}
	
	public String serverAddress(){
		return keepassEnv.getPropertyByName(SERVER_ADDRESS).getValue();
	}
	
	public String serverUsername(){
		return keepassEnv.getPropertyByName(SERVER_USERNAME).getValue();
	}
	
	public String serverPassword(){
		return keepassEnv.getPropertyByName(SERVER_PASSWORD).getValue();
	}
	
	/**
	 * Path where remote files are stored in FTP
	 * Example: /JKP/
	 * @return
	 */
	public String remotePath(){
		return this.keepassEnv.getPropertyByName(REMOTE_PATH).getValue();
	}	
	
	public Integer notifExportFailedUser(){
		return Integer.parseInt(this.keepassEnv.getPropertyByName(NOTIF_EXPORT_FAILED_USR).getValue());
	}
	
	public Integer notifExportFailedSub(){
		return Integer.parseInt(this.keepassEnv.getPropertyByName(NOTIF_EXPORT_FAILED_SUB).getValue());
	}
	
	public Integer notifImportFailedUser(){
		return Integer.parseInt(this.keepassEnv.getPropertyByName(NOTIF_IMPORT_FAILED_USR).getValue());
	}
	
	public Integer notifImportFailedSub(){
		return Integer.parseInt(this.keepassEnv.getPropertyByName(NOTIF_IMPORT_FAILED_SUB).getValue());
	}
	
	public String notifImportFailedTemplate(){
		return this.keepassEnv.getPropertyByName(NOTIF_IMPORT_FAILED_TPL).getValue();
	}
	
	public String notifExportFailedTemplate(){
		return this.keepassEnv.getPropertyByName(NOTIF_EXPORT_FAILED_TPL).getValue();
	}

	public String keepassTitle(){
		return KEEPASSTILE;
	}
	
	public Integer maxFilesDownload(){
		return MAX_FILES_DOWNLOAD;
	}

}

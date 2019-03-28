package br.com.tlmgroup.jkp.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ImportResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportResponse {
	
	private List<JKPFileStatus> jkpFileStatusList;
	private String  importFromFtp;
	private String  sendImportErrorNotification;
	private String  deleteFtpFiles;
	private String  importMessage;
	private String  ftpResponse;
	private Integer processedFiles = 0;
	
	public ImportResponse(String importFromFtp, String deleteFtpFiles, String sendImportErrorNotification){
		this.importFromFtp					= importFromFtp;
		this.deleteFtpFiles					= deleteFtpFiles;
		this.sendImportErrorNotification	= sendImportErrorNotification;
	}
	
	public List<JKPFileStatus> getJkpFileStatusList() {
		return jkpFileStatusList;
	}
	public void setJkpFileStatusList(List<JKPFileStatus> jkpFileStatus) {
		this.jkpFileStatusList = jkpFileStatus;
		this.setProcessedFiles(jkpFileStatus.size());
	}

	public String getImportMessage() {
		return importMessage;
	}
	public void setImportMessage(String importMessage) {
		this.importMessage = importMessage;
	}
	public String getFtpResponse() {
		return ftpResponse;
	}
	public void setFtpResponse(String ftpResponse) {
		this.ftpResponse = ftpResponse;
	}
	public Integer getProcessedFiles() {
		return processedFiles;
	}
	public void setProcessedFiles(Integer processedFiles) {
		this.processedFiles = processedFiles;
	}
	public String getDeleteFtpFiles() {
		return deleteFtpFiles;
	}

	public void setDeleteFtpFiles(String deleteFtpFiles) {
		this.deleteFtpFiles = deleteFtpFiles;
	}

	public String getImportFromFtp() {
		return importFromFtp;
	}

	public void setImportFromFtp(String importFromFtp) {
		this.importFromFtp = importFromFtp;
	}

	public String getSendImportErrorNotification() {
		return sendImportErrorNotification;
	}

	public void setSendImportErrorNotification(String sendImportErrorNotification) {
		this.sendImportErrorNotification = sendImportErrorNotification;
	}
}

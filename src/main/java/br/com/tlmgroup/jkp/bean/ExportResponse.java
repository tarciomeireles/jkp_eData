package br.com.tlmgroup.jkp.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ExportResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExportResponse {
	
	
	private List<JKPDataExport> jkpExportList;
	private String  sendExportErrorNotification;
	private String  exportMessage = "";
	private Integer processedRows = 0;
	
	public ExportResponse(String sendExportErrorNotification){
		this.sendExportErrorNotification = sendExportErrorNotification;
	}
	
	public List<JKPDataExport> getJkpExportList() {
		return jkpExportList;
	}
	public void setJkpExportList(List<JKPDataExport> jkpExportList) {
		this.jkpExportList = jkpExportList;
	}
	public String getSendExportErrorNotification() {
		return sendExportErrorNotification;
	}
	public void setSendExportErrorNotification(String sendExportErrorNotification) {
		this.sendExportErrorNotification = sendExportErrorNotification;
	}
	public String getExportMessage() {
		return exportMessage;
	}
	public void setExportMessage(String exportMessage) {
		this.exportMessage = exportMessage;
	}
	public Integer getProcessedRows() {
		return processedRows;
	}
	public void setProcessedRows(Integer processedRows) {
		this.processedRows = processedRows;
	}

}

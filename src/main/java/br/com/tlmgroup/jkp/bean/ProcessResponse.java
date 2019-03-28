package br.com.tlmgroup.jkp.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ExportResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessResponse {
	private ImportResponse importResponse;
	private ExportResponse exportResponse;
	
	public ImportResponse getImportResponse() {
		return importResponse;
	}
	public void setImportResponse(ImportResponse importResponse) {
		this.importResponse = importResponse;
	}
	public ExportResponse getExportResponse() {
		return exportResponse;
	}
	public void setExportResponse(ExportResponse exportResponse) {
		this.exportResponse = exportResponse;
	}
	

}

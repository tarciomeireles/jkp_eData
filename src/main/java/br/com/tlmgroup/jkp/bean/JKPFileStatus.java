package br.com.tlmgroup.jkp.bean;

import java.sql.Date;
import java.text.DecimalFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.tlmgroup.jkp.helper.AbstractUniversal;

@XmlRootElement(name="JKPFileStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class JKPFileStatus extends AbstractUniversal{
	
	public static final String FAILED = "FAILED";
	public static final String IMPORTED = "IMPORTED";
	
	private Integer statusId;
	private Integer jkpId;
	private String  status;
	private String  md5;
	private String  filename;
	private Long    size;
	private Integer lines;
	private String  message;
	private Date    createAt;
	
	
	public Integer getStatusId() {
		return statusId;
	}
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}
	public Integer getJkpId() {
		return jkpId;
	}
	public void setJkpId(Integer jkpId) {
		this.jkpId = jkpId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Integer getLines() {
		return lines;
	}
	public void setLines(Integer lines) {
		this.lines = lines;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	public String getSizeFriendlyFormat() {
	    String hrSize = null;

	    double b = this.size;
	    double k = this.size/1024.0;
	    double m = ((this.size/1024.0)/1024.0);
	    double g = (((this.size/1024.0)/1024.0)/1024.0);
	    double t = ((((this.size/1024.0)/1024.0)/1024.0)/1024.0);

	    DecimalFormat dec = new DecimalFormat("0.00");

	    if ( t > 1 ) {
	        hrSize = dec.format(t).concat(" TB");
	    } else if ( g > 1 ) {
	        hrSize = dec.format(g).concat(" GB");
	    } else if ( m > 1 ) {
	        hrSize = dec.format(m).concat(" MB");
	    } else if ( k > 1 ) {
	        hrSize = dec.format(k).concat(" KB");
	    } else {
	        hrSize = dec.format(b).concat(" Bytes");
	    }

	    return hrSize;
	}
	
	@Override
	public String toString(){
		return "JKPFileStatus [statusId=" + statusId
				+ ", jkpId=" + jkpId 
				+ ", status=" + status
				+ ", md5=" + md5
				+ ", filename=" + filename
				+ ", size=" + size
				+ ", lines=" + message
				+ "]";
	}
}
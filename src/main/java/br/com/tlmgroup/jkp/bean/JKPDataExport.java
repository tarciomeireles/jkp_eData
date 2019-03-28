package br.com.tlmgroup.jkp.bean;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="JKPDataExport")
@XmlAccessorType(XmlAccessType.FIELD)
public class JKPDataExport {
	
	
	public static final String CONTENTION = "CONTENTION";
	public static final String EXPORTED   = "EXPORTED";
	public static final String NOT_SENT = "NOT_SENT";

	private Integer subscriptionId;
	private Integer txId;
	private Integer rxId;
	private Integer notId;
	private Double rxVolume;
	private Double txVolume;
	private Double notVolume;
	private String status = NOT_SENT;
	private String exportMessage = ""; //Success, Contention, Invalid Subscriptions, Invalid JKP Subscription messages
	private SubscriptionStatus subscriptionStatus;

	
	public JKPDataExport(Integer subscriptionId, Double rxVolume, Double  txVolume, Double notVolume, Map<String, Integer> jkpResourceIds){
		this.setSubscriptionId(subscriptionId);
		this.setRxVolume(rxVolume);
		this.setTxVolume(txVolume);
		this.setNotVolume(notVolume);
		this.setTxId(jkpResourceIds.get("TX"));
		this.setRxId(jkpResourceIds.get("RX"));
		this.setNotId(jkpResourceIds.get("NOT"));
	}
	
	public Integer getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Integer subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public double getRxVolume() {
		return rxVolume;
	}
	public void setRxVolume(double rxVolume) {
		this.rxVolume = rxVolume;	}

	public double getTxVolume() {
		return txVolume;
	}
	public void setTxVolume(double txVolume) {
		this.txVolume = txVolume;
	}
	public double getNotVolume() {
		return notVolume;
	}
	
	public void setNotVolume(double notVolume) {
		this.notVolume = notVolume;
	}


	public boolean isEmpty(){
		if(this.subscriptionId == null && this.rxVolume == null && this.txVolume == null && this.notVolume == null){
			return false;
		}
		return true;
	}	
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	

	public String getExportMessage() {
		return exportMessage;
	}
	
	public void setExportMessage(String exportMessage) {
		this.exportMessage = exportMessage;
	}

	public Integer getTxId() {
		return txId;
	}

	public void setTxId(Integer txId) {
		this.txId = txId;
	}

	public Integer getRxId() {
		return rxId;
	}

	public void setRxId(Integer rxId) {
		this.rxId = rxId;
	}

	public Integer getNotId() {
		return notId;
	}

	public void setNotId(Integer notId) {
		this.notId = notId;
	}

	public SubscriptionStatus getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

}

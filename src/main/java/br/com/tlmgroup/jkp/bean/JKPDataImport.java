package br.com.tlmgroup.jkp.bean;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="JKPDataImport")
@XmlAccessorType(XmlAccessType.FIELD)
public class JKPDataImport {
	
	private Integer subscriptionId;
	private String  mailboxId;
	private Integer consumptionRx;
	private Integer consumptionTx;
	private Integer consumptionNot;
	private Date 	consumptionDate;
	
	public Date getConsumptionDate() {
		return consumptionDate;
	}
	public void setConsumptionDate(Date consumptionDate) {
		this.consumptionDate = consumptionDate;
	}
	
	public Integer getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Integer subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public Integer getConsumptionRx() {
		return consumptionRx;
	}
	public void setConsumptionRx(Integer consumptionRx) {
		this.consumptionRx = consumptionRx;
	}
	public Integer getConsumptionTx() {
		return consumptionTx;
	}
	public void setConsumptionTx(Integer consumptionTx) {
		this.consumptionTx = consumptionTx;
	}
	public Integer getConsumptionNot() {
		return consumptionNot;
	}
	public void setConsumptionNot(Integer consumptionNot) {
		this.consumptionNot = consumptionNot;
	}
	public String getMailboxId() {
		return mailboxId;
	}
	public void setMailboxId(String mailboxId) {
		this.mailboxId = mailboxId;
	}
}
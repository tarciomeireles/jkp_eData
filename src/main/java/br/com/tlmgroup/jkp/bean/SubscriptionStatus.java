package br.com.tlmgroup.jkp.bean;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import br.com.tlmgroup.jkp.helper.AbstractUniversal;

@XmlRootElement
public class SubscriptionStatus extends AbstractUniversal {

	private Integer	subscriptionId;
	private Integer servStatus = 0;
	private Integer status = 0;
	private boolean active = false;
	public static final Integer SERVERSTATUS_EXEC 	= 50;
	public static final Integer STATUS_ACTIVE 		= 30;
	public static final Integer STATUS_ACTIVETECH   = 40;
	

	
	public void setSubscriptionStatus(Map<String, Object> subscriptionStatusMap){
		log.info(">>> .setSubscriptionStatus " + subscriptionId);
		this.setSubscriptionId(castObject(subscriptionStatusMap.get("SubscriptionID")));
		this.setServStatus(castObject(subscriptionStatusMap.get("ServStatus")));
		this.setStatus(castObject(subscriptionStatusMap.get("Status")));
		setActive(verifyActive());
		log.info("<<< .setSubscriptionStatus: " + subscriptionId + " isActive:" + isActive());
	}


	public Integer getSubscriptionId() {
		return subscriptionId;
	}


	public void setSubscriptionId(Integer subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	/**
	* 10	Não Provisionado                                  
	* 20	Provisionando                                     
	* 30	Parado                                            
	* 40	Iniciando                                         
	* 50	Em Execução                                       
	* 60	Parando                                           
	* 70	Removendo                                         
	* 80	Mudando de Plano                                  
	* 90	Removido
	*/  
	public Integer getServStatus() {
		return servStatus;
	}


	public void setServStatus(Integer servStatus) {
		this.servStatus = servStatus;
	}

	/**
	 * 10	Em processamento
	 * 15	Avaliação
	 * 30	Ativo
	 * 40	Ativado Tecnicamente
	 * 50	Expirado
	 * 60	Desativado
	 * 70	Cancelado
	 * 80	Suspensão administrativa
	 * 85	Crédito Suspenso
	 * 89	Suspensão de crédito+administrativa
	 */
	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}
		
	
	private boolean verifyActive(){
		return getServStatus() == SERVERSTATUS_EXEC && ( getStatus() == STATUS_ACTIVE || getStatus() == STATUS_ACTIVETECH); 
	}
	
	
	@Override
	public String toString() {
		return "SubscriptionStatus [subscriptionId=" + subscriptionId
				+ ", servStatus=" + servStatus + ", status=" + status + "]";
	}
	
	private Integer castObject(Object obj){
		String s = obj.toString();
		return Integer.valueOf(s.substring(0, s.indexOf('.')));
	}
}
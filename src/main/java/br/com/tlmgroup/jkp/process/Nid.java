package br.com.tlmgroup.jkp.process;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.tlmgroup.commons.client.nid.BusinessAutomationClient;
import br.com.tlmgroup.commons.client.nid.bean.SubscriptionResource;
import br.com.tlmgroup.dbenvironment.DbLibEnvironment;
import br.com.tlmgroup.jkp.bean.SubscriptionStatus;
import br.com.tlmgroup.jkp.helper.AbstractUniversal;


public class Nid extends AbstractUniversal{
	
	BusinessAutomationClient baClient = new BusinessAutomationClient();
	Integer jkpTXId  = null;
	Integer jkpNOTId = null;
	Integer jkpRXId  = null;
	
	public Nid(){
		DbLibEnvironment dbEnv;
		try {
			dbEnv = new DbLibEnvironment();
			this.jkpTXId  = dbEnv.getValueAsInteger("JKP.RESOURCE.ID.TX");
			this.jkpNOTId = dbEnv.getValueAsInteger("JKP.RESOURCE.ID.NOT");
			this.jkpRXId  = dbEnv.getValueAsInteger("JKP.RESOURCE.ID.RX");
		} catch (Exception e){
			log.error("Unable to inialize DbLibEnvironment: " + e.getMessage());
		}
	}
	
	
	private List<SubscriptionResource> getResourcesFromSub(Integer subscriptionId){
		List<SubscriptionResource> resourceMap = new ArrayList<>();

		try {
			resourceMap = baClient.getSubscriptionsResourceBySubscriptionId(subscriptionId);
		} catch (Exception e) {
			log.error("ConsumptionService getResourcesFromSub ERROR: " + e.getMessage());
		}
		
		return resourceMap;
	}
	
	private List<Integer> getResourceIdListFromSubscription(Integer subscriptionId){
		log.debug(">>> getResourceIdListFromSubscription: " + subscriptionId);
		List<Integer> resourceIds = new ArrayList<>();
		
		List<SubscriptionResource> resources  = this.getResourcesFromSub(subscriptionId);
	
		for (SubscriptionResource resource: resources){
			resourceIds.add(resource.getResourceID());
		}
		log.debug("<<< getResourceIdListFromSubscription Subscription:" + subscriptionId + " ResourceIDs: " + resourceIds.toString());
		return resourceIds;
	}
	
	/**
	 * Verify if the subscriptionID has the JKP resourceIDs RX, TX and NOT
	 * @param subscriptionId
	 * @return boolean
	 */
	public boolean validJKPSubscription(Integer subscriptionId){
		log.info(">>> validJKPSubscription: " + subscriptionId);
		boolean isJkp = false;
		List<Integer> resourceIds =  getResourceIdListFromSubscription(subscriptionId);
		
		if(!resourceIds.contains(this.jkpTXId)){
			log.error(String.format("Resource TX (ResourceID %d) for subscription %d not found", this.jkpTXId ,subscriptionId));
		}else if(!resourceIds.contains(this.jkpRXId)){
			log.error(String.format("Resource RX (ResourceID %d) for subscription %d not found", this.jkpRXId , subscriptionId));
		}else if(!resourceIds.contains(this.jkpNOTId)){
			log.error(String.format("Resource NOT (ResourceID %d) for subscription %d not found", this.jkpNOTId , subscriptionId));
		}else{
			isJkp = true;
		}

		log.info("<<< validJKPSubscription: " + subscriptionId + " JKP:" + isJkp);
		return isJkp;
	}
	
	public SubscriptionStatus getSubscriptionStatus(Integer subscriptionId){
		log.info(">>> getSubscriptionStatus: " + subscriptionId);
		SubscriptionStatus subscriptionStatus = new SubscriptionStatus();
		try{
			Map<String, Object> subscriptionStatusMap = baClient.getSubscriptionDetails(subscriptionId);
			subscriptionStatus.setSubscriptionStatus(subscriptionStatusMap);
		}catch(Exception e){
			log.error("<<< getSubscriptionStatus: Unable to get Subscription Information" );
		}
		log.info("<<< getSubscriptionStatus " + subscriptionId + " Status:" + subscriptionStatus.getStatus() + " ServStatus:" + subscriptionStatus.getServStatus());
		return subscriptionStatus;
	}
}

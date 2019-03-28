package br.com.tlmgroup.jkp.process;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.tlmgroup.commons.client.odin.BusinessAutomationClient;
import br.com.tlmgroup.commons.client.odin.exception.NidErrorException;
import br.com.tlmgroup.commons.client.utils.UtilsClient;
import br.com.tlmgroup.jkp.bean.JKPDataExport;
import br.com.tlmgroup.jkp.bean.ExportResponse;
import br.com.tlmgroup.jkp.bean.SubscriptionStatus;
import br.com.tlmgroup.jkp.dao.ExportDAO;
import br.com.tlmgroup.jkp.helper.AbstractUniversal;
import br.com.tlmgroup.jkp.process.Nid;

import com.google.gson.JsonObject;

public class Export extends AbstractUniversal{
	
	public static final String SEND_EXPORT_ERROR_NOTIFICATIONS	= "y";
	
	UtilsClient utilsClient;
	BusinessAutomationClient baClient;
	Nid odin;
	ExportDAO exDao;
	
	public Export(){
		this.baClient = new BusinessAutomationClient();
		this.utilsClient = new UtilsClient();
		this.odin = new Nid();
		this.exDao = new ExportDAO();
	}
	
	/**
	 * Process the JKP not send registers in Consumption Table and export data to Nid or, 
	 * in case of export error, saves in Contention Table
	 * @return 
	 * @throws Exception 
	 */
	public List<JKPDataExport> exportJkp(){
		log.info(">>> ExportProccess exportJkp" );

		List<JKPDataExport> jkpDataExportList  = new ArrayList<>();
		
		JsonObject jsonResponse = null;
		
		try{
			jkpDataExportList = exDao.getJkpDataExportList();

			for(JKPDataExport jkpDataExport: jkpDataExportList){
				
				SubscriptionStatus subscriptionStatus = odin.getSubscriptionStatus(jkpDataExport.getSubscriptionId());
				jkpDataExport.setSubscriptionStatus(subscriptionStatus);

				String jsonExportData = setJsonExport(jkpDataExport);

				if(subscriptionStatus.isActive()){

					if(odin.validJKPSubscription(jkpDataExport.getSubscriptionId())){
						/*
						 * Active and Valid JKP subscription 
						 */
						try{
							jsonResponse = baClient.incrementResourceUsage(jsonExportData);
							jkpDataExport.setStatus(JKPDataExport.EXPORTED);
							jkpDataExport.setExportMessage(jsonResponse.get("resources").getAsString());
							this.exDao.setConsumptionAsSent(jkpDataExport.getSubscriptionId());
						}catch(NidErrorException odinErrorException){
							exportToContention(jkpDataExport, odinErrorException.getMessage());
						}
					}else{
						/*
						 * Send to Contention - Subscription haven't JKP resources  
						 */
						exportToContention(jkpDataExport, "Subscription haven't JKP resources");
					}
				}else{
					/**
					 * Send to Contention - Subscription is not active   
					 */
					exportToContention(jkpDataExport, "Subscription is not active");
				}
			}		
		}catch(SQLException sqlException){
			log.error("Error while retriving data to export", sqlException);
		}catch(Exception e){
			log.error("Error while retriving data to export", e);
		}
		
		sendNotificationIfExistsContentions(jkpDataExportList);
		log.info("<<< ExportProccess exportJkp:  " + jkpDataExportList.size());
		return jkpDataExportList;
	}
	
	
	public void exportToContention(JKPDataExport jkpDataExport, String message) throws SQLException{
		jkpDataExport.setExportMessage(message);
		jkpDataExport.setStatus(JKPDataExport.CONTENTION);
		if(this.exDao.insertJKPConsuptionIntoContent(jkpDataExport) > 0){
			this.exDao.setConsumptionAsSent(jkpDataExport.getSubscriptionId());
		}else{
			log.error("Error while inserting Data on contention, the consumption was not marked as sent");
		}
	}
	
	
	/**
	 * If exists a JKPDataExport with status = CONTENTION send a notification 
	 * using configurations stored in keepass:
	 * - notifExportFailedTemplate - Notification Template to Export (set in Nid) 
	 * - SubscriptionId responsible for receive notification
	 * - UserId responsible for receive notification
	 * @param jkpDataExportList
	 * @return String with the success message from Nid or error message generated in method
	 */
	public String sendNotificationIfExistsContentions(List<JKPDataExport> jkpDataExportList){
		log.debug(">>> sendNotificationIfExistsContentions");
		String notificationReponse = "";
		HashMap<String, String> notificationTags = new HashMap<>();
		
		List<String> contentionSubscriptions = getContentionSubscriptions(jkpDataExportList);
		
		if(!contentionSubscriptions.isEmpty()){
			String contentionSubscription = String.join(", ", contentionSubscriptions);
			notificationTags.put("JKP_subscription_error_list", contentionSubscription);
			try {
				notificationReponse = utilsClient.sendNotification(cfg().notifExportFailedTemplate(), cfg().notifExportFailedSub(), cfg().notifExportFailedUser(), notificationTags);
			} catch (Exception e) {
				notificationReponse = "Unable to send notification for Contentions, Subscriptions:[" + contentionSubscription + "] "
						+ "template: [" + cfg().notifExportFailedTemplate() + "]"
						+ "subscription used to send notifications: [" + cfg().notifExportFailedSub() + "]"
						+ "subscription used to send notifications: [" + cfg().notifExportFailedUser() + "]";
				log.error(notificationReponse, e);
			}
			
		}else{
			log.debug("No Contention Notifications to send");
		}
		log.debug("<<< sendNotificationIfExistsContentions contentions: " + contentionSubscriptions.size());
		return notificationReponse;
	}
	
	
	/**
	 * Returns a List of subscriptions if the JKPDataExport is equal CONTENTION
	 * @param jkpDataExportList
	 * @return List of Subscription (casted to String)
	 */
	private List<String> getContentionSubscriptions(List<JKPDataExport> jkpDataExportList){
		List<String> subscriptionContentionList = new ArrayList<>();
		jkpDataExportList.forEach(jkpData -> {
			if(jkpData.getStatus().equals(JKPDataExport.CONTENTION)){
				subscriptionContentionList.add(jkpData.getSubscriptionId().toString());
			}			
		});
		return subscriptionContentionList;
	}
	
	
	private String setJsonExport(JKPDataExport jkpDataExport){
		return "{" 
				+ "\"resourceUsage\":{"
				+ 	"\"subscriptionId\" : " + jkpDataExport.getSubscriptionId() + ","
				+ 	"\"resourceDelta\": ["
				+ 	"{"	
				+ 		"\"resourceId\" : " + jkpDataExport.getTxId() + ","
				+ 		"\"amount\" : " + jkpDataExport.getTxVolume()
				+ "},"
				+ "{"	
				+ 	"\"resourceId\" : " + jkpDataExport.getRxId() + ","
				+ 	"\"amount\" : " + jkpDataExport.getRxVolume() 
				+ "},{"	
				+ 	"\"resourceId\" : " + jkpDataExport.getNotId() + ","
				+ 	"\"amount\" : " + jkpDataExport.getNotVolume()   
				+ "}]}}";
	}
	
	public ExportResponse exportProcess(String sendExportErrorNotification) throws Exception{
		
		ExportResponse exportResponse = new ExportResponse(sendExportErrorNotification);
		Export exportJkp = new Export();
		List<JKPDataExport> jkpExportList = new ArrayList<>();
		
		if(!sendExportErrorNotification.equals(Export.SEND_EXPORT_ERROR_NOTIFICATIONS)){
			exportResponse.setSendExportErrorNotification("No error notifications will be send");
		}
		
		try {
			jkpExportList = exportJkp.exportJkp();
			if(jkpExportList.isEmpty()){
				exportResponse.setExportMessage("No JKP data to export");
			}else{
				exportResponse.setJkpExportList(jkpExportList);
				exportResponse.setExportMessage("JKP export data size:" + jkpExportList.size());
				exportResponse.setProcessedRows(jkpExportList.size());
			}
		} catch (Exception e) {
			log.error("Error on export JKP data: " + e.getMessage());
			throw e;
		}

		return exportResponse;
	}
}

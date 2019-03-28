package br.com.tlmgroup.jkp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.sql.DataSource;

import br.com.tlmgroup.commons.datasource.DataSourceFactory;
import br.com.tlmgroup.dbenvironment.DbLibEnvironment;
import br.com.tlmgroup.jkp.bean.JKPDataExport;
import br.com.tlmgroup.jkp.helper.AbstractUniversal;

public class ExportDAO extends AbstractUniversal{

	private DataSource ds;
	private String jndi = "java:comp/env/jdbc/jkp";
	private  Map<String, Integer> jkpResourceIDs = new HashMap<>();

	public ExportDAO(){
		try {
			this.ds = DataSourceFactory.getDataSource(jndi);
			dbLibEnvironmentInitialize();
		} catch (NamingException e) {
			log.error("Unable to connect at " + jndi, e);
		}
	}
	
	
	private void dbLibEnvironmentInitialize(){
		DbLibEnvironment dbEnv;
		try {
			dbEnv = new DbLibEnvironment();
			jkpResourceIDs.put("TX",  dbEnv.getValueAsInteger("JKP.RESOURCE.ID.TX"));
			jkpResourceIDs.put("RX",  dbEnv.getValueAsInteger("JKP.RESOURCE.ID.RX"));
			jkpResourceIDs.put("NOT", dbEnv.getValueAsInteger("JKP.RESOURCE.ID.NOT"));
		} catch (NamingException e) {
			log.error("Unable to connect to DbLibEnvironment review the JNDI java:comp/env/jdbc/report/telcom", e);
		} catch (Exception e) {
			log.error("Unable to get IDs information of JKP.RESOURCE.ID.[TX, NOT, RX] from environment table", e);
		}
	}
	
	public List<JKPDataExport> getJkpDataExportList(){
		log.info(">>> getJkpDataExportList");
		
		List<JKPDataExport> jkpDataExportList = new ArrayList<>(); 

		String sqlFile = "SELECT `subscription`,"
				+ "ROUND(SUM(`rx_volume`) / POW(1024, 2), 3) as rx_volume, "
				+ "ROUND(SUM(`tx_volume`) / POW(1024, 2), 3) as tx_volume"
				+ "ROUND(SUM(`not_volume`) / POW(1024, 2), 3) as not_volume"
				+ "FROM `consumption` "
				+ "WHERE (`is_sent` IS NULL OR `is_sent` = '0000-00-00 00:00:00')"
				+ "GROUP BY `subscription`";
		
		
		try(Connection con = this.ds.getConnection();
			PreparedStatement stmt = con.prepareStatement(sqlFile)){
			
			ResultSet rs = stmt.executeQuery();

			while(rs.next()){
				jkpDataExportList.add(new JKPDataExport(
						rs.getInt("subscription"), 
						rs.getDouble("rx_volume"), 
						rs.getDouble("tx_volume"),
						rs.getDouble("not_volume"), 
						jkpResourceIDs)
				);
			}
			
			rs.close();
			
		} catch (SQLException e) {
			log.error("<<<< ExportDAO getJkpDataExportList Error while getResultSet:", e);
		}
		log.info("<<< getJkpDataExportList: " + jkpDataExportList.size() + " registers found"); 
		return jkpDataExportList;
	}
	
	
	public Integer insertJKPConsuptionIntoContent(JKPDataExport jkpDataExport) throws SQLException{
		log.debug(">>> ExportDAO insertJKPConsuptionIntoContent Subscription:" + jkpDataExport.getSubscriptionId() + " " + jkpDataExport.getExportMessage());
		
		int affectedRows = 0;
		int contentionId = 0;
		
		String sqlFile = "INSERT INTO `contention` SET "
				+ "subscription = ?, "
				+ "rx_id = ?, "
				+ "tx_id = ?, "
				+ "not_id = ?, "
				+ "rx_amount = ?, "
				+ "tx_amount = ?, "
				+ "not_amount = ?, "
				+ "error_message = ?, "
				+ "subscription_status = ?, "
				+ "subscription_servstatus = ?, "
				+ "created_at = NOW(), "
				+ "notificated_at = NOW() ";
		
		
		try(Connection con = this.ds.getConnection();
				PreparedStatement stmt = con.prepareStatement(sqlFile, PreparedStatement.RETURN_GENERATED_KEYS)){
			
			int i = 1;
			stmt.setInt(i++, jkpDataExport.getSubscriptionId());
			stmt.setInt(i++, jkpDataExport.getRxId());
			stmt.setInt(i++, jkpDataExport.getTxId());
			stmt.setInt(i++, jkpDataExport.getNotId());
			stmt.setDouble(i++, jkpDataExport.getRxVolume());
			stmt.setDouble(i++, jkpDataExport.getTxVolume());
			stmt.setDouble(i++, jkpDataExport.getNotVolume());
			stmt.setString(i++, jkpDataExport.getExportMessage());
			stmt.setInt(i++, jkpDataExport.getSubscriptionStatus().getStatus());
			stmt.setInt(i++, jkpDataExport.getSubscriptionStatus().getServStatus());
			
			affectedRows = stmt.executeUpdate();
			
			ResultSet rsKeys = stmt.getGeneratedKeys();
			if(rsKeys.next())
				contentionId = rsKeys.getInt(1);
			
		}catch (Exception e) {
			log.error(">>> ExportDAO insertJKPConsuptionIntoContent:" + e.getMessage());
		}
		log.debug("<<< ExportDAO insertJKPConsuptionIntoContent Affected Rows: " + affectedRows + " contention_id:" + contentionId);

		return contentionId;
	}
	
	public Integer setConsumptionAsSent(Integer subscription){
		log.debug(">>> setConsumptionAsSent, subscription:" + subscription);
		Integer affectedRows = 0;
		String sql = "UPDATE consumption SET `is_sent` = NOW() WHERE `subscription` = ? AND (`is_sent` IS NULL OR `is_sent` = '0000-00-00 00:00:00')";
		try(Connection con = this.ds.getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);){
			stmt.setInt(1, subscription);
			affectedRows = stmt.executeUpdate();
		}catch(SQLException e){
			log.error("<<< setConsumptionAsSent - Error while updating table consumption", e);
		}
		log.debug("<<< setConsumptionAsSent, " + affectedRows + " affected rows");
		return affectedRows;
	}	

}

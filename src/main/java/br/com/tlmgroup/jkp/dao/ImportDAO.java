package br.com.tlmgroup.jkp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.naming.NamingException;
import javax.sql.DataSource;

import br.com.tlmgroup.commons.datasource.DataSourceFactory;
import br.com.tlmgroup.jkp.bean.JKPDataImport;
import br.com.tlmgroup.jkp.bean.JKPFileStatus;
import br.com.tlmgroup.jkp.helper.AbstractUniversal;

public class ImportDAO extends AbstractUniversal{
	
	/*
	 * Should the driver compensate for the update counts of "ON DUPLICATE KEY" INSERT statements (2 = 1, 0 = 1) when using prepared statements?
	 */
	private static final int UPDATED_ON_DUPLICATE_KEY = 2; 

	private DataSource ds;
	Connection con = null;
	private String jndi = "java:comp/env/jdbc/jkp";
	PreparedStatement stmtJkpData = null;
	SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-mm-dd 00:00:00");


	public ImportDAO() throws SQLException{
		try {
			ds = DataSourceFactory.getDataSource(jndi);
		} catch (NamingException e) {
			log.error("<<< ConsumptionDAO Unable to get JNDI: " +  jndi, e);
		}
	}
	
	public void openConnection() throws SQLException{
		con = this.ds.getConnection();
	}

	/**
	 * Insert a new file on table jkp_file if the jkp_identification already exists  updates 
	 * @param jkpFileName
	 * @return The last insert/updated ID
	 * @throws SQLException
	 */
	public Integer insertJKPFile(String jkpFileName) throws SQLException{
		String sqlFile = "INSERT INTO jkp_file SET "
				+ "jkp_identification = ?,"
				+ "created_at = NOW(),"
				+ "updated_at = NOW() "
				+ "ON DUPLICATE KEY UPDATE updated_at = NOW()";
		Integer insertId = null;
		Integer onDuplicateKey = null;
		
		try(PreparedStatement stmt = con.prepareStatement(sqlFile, PreparedStatement.RETURN_GENERATED_KEYS);){
			stmt.setString(1, jkpFileName);
		
			onDuplicateKey   = stmt.executeUpdate();
			ResultSet rsKeys = stmt.getGeneratedKeys();
			if(rsKeys.next())
				insertId = rsKeys.getInt(1);
			rsKeys.close();
			
		}catch (Exception e) {
			log.error("insertJKPFile:" + e);
		}
		if(onDuplicateKey == UPDATED_ON_DUPLICATE_KEY){
			log.info("insertJKPFile an existing row was updated: " + insertId);
		}else{
			log.info("insertJKPFile a new row was inserted: " + insertId);
		}
		return insertId;

	}
	
	/**
	 * Add a new consumption line to jkpId 
	 * This method not commits the data
	 * @param jkpId
	 * @param jkpData
	 * @return
	 * @throws SQLException
	 */
	public Integer jkpAddConsumption(Integer jkpId, JKPDataImport jkpData) throws SQLException{
		String sqlConsumption = "INSERT INTO consumption SET "
				+ "jkp_id       = ?,"
				+ "traffic_date = ?,"
				+ "subscription = ? ,"
				+ "mailbox_id = ? ,"
				+ "tx_volume =? ,"
				+ "rx_volume =? ,"
				+ "not_volume= ? ";

		Integer insertId = null;
		stmtJkpData = con.prepareStatement(sqlConsumption);
		try{
			con.setAutoCommit(false);
			
			int i = 1;
			stmtJkpData.setInt(i++, jkpId);
			stmtJkpData.setString(i++, mysqlDateFormat.format(jkpData.getConsumptionDate()));
			stmtJkpData.setInt(i++, jkpData.getSubscriptionId());
			stmtJkpData.setString(i++, jkpData.getMailboxId());
			stmtJkpData.setInt(i++, jkpData.getConsumptionTx());
			stmtJkpData.setInt(i++, jkpData.getConsumptionRx());
			stmtJkpData.setInt(i, jkpData.getConsumptionNot());
			stmtJkpData.execute();
			
		}catch (Exception e) {
			log.error("<<< jkpAddConsumption error while inserting :" + e);
		}

		return insertId;
	}
	
	/**
	 * Commits the data 
	 * @throws SQLException
	 */
	public void jkpCommitConsumption() throws SQLException{
		log.info(">>> jkpCommitConsumption");
		con.commit();
		con.setAutoCommit(true);
		stmtJkpData.close();
		log.info("<<< jkpCommitConsumption");
	}
	
	/**
	 * rollback the data 
	 */
	public void jkpRollback(){
		log.info(">>> jkpRollback");
		try{
			con.rollback();
			con.setAutoCommit(true);
		}catch(SQLException e){
			log.error("jkpRollback Could not execute rollback and/or close connection", e);
		}
		log.info("<<< jkpRollback");
	}
	
	/**
	 * Close connection at the end of process 
	 * @throws SQLException
	 */
	public void close() throws SQLException{
		con.close();
	}
	
	/**
	 * Insert success status on jkp_status 
	 * @param jkpId
	 * @param jkpStatus
	 * @param message
	 * @return
	 */
	public JKPFileStatus jkpInsertStatusSuccess(Integer jkpId, JKPFileStatus jkpStatus, String message){
		log.info(">>> jkpInsertStatusSuccess");
		jkpStatus.setStatus(JKPFileStatus.IMPORTED);
		JKPFileStatus jkpStatusReturn = jkpInsertStatus(jkpId, jkpStatus, message);
		log.info("<<< jkpInsertStatusSuccess");
		return jkpStatusReturn;
	}
	
	/**
	 * Insert failed status on jkp_status 
	 * @param jkpId
	 * @param jkpStatus
	 * @param message
	 * @return
	 */
	public JKPFileStatus jkpInsertStatusFailed(Integer jkpId, JKPFileStatus jkpStatus, String message){
		log.info(">>> jkpInsertStatusFailed");
		jkpStatus.setStatus(JKPFileStatus.FAILED);
		JKPFileStatus jkpStatusReturn = jkpInsertStatus(jkpId, jkpStatus, message);
		log.info("<<< jkpInsertStatusFailed");
		return jkpStatusReturn;
	}
	
	/**
	 * Insert success/failed status on jkp_status 
	 * @param jkpId
	 * @param jkpStatus
	 * @param message
	 * @return
	 */
	private JKPFileStatus jkpInsertStatus(Integer jkpId, JKPFileStatus jkpStatus, String message){
		log.info(">>> jkpInsertStatus");
		
		jkpStatus.setJkpId(jkpId);
		jkpStatus.setMessage(message);
		
		String sqlInsertStatus = "INSERT INTO jkp_status SET "
				+ "`jkp_id` = ?, "
				+ "`status` = ?, "
				+ "`md5` = ? , "
				+ "`filename` = ? , "
				+ "`size` = ? , "
				+ "`lines` = ? ,"
				+ "`message` = ? ,"
				+ "`create_at` = NOW()";
		try(PreparedStatement stmt = con.prepareStatement(sqlInsertStatus, PreparedStatement.RETURN_GENERATED_KEYS);){
			int i=1;
			stmt.setInt(i++, jkpStatus.getJkpId());
			stmt.setString(i++, jkpStatus.getStatus());
			stmt.setString(i++, jkpStatus.getMd5());
			stmt.setString(i++, jkpStatus.getFilename());
			stmt.setLong(i++, jkpStatus.getSize());
			stmt.setInt(i++, jkpStatus.getLines());
			stmt.setString(i++, jkpStatus.getMessage());
			stmt.execute();

			ResultSet rsKeys = stmt.getGeneratedKeys();
			if(rsKeys.next())
				jkpStatus.setStatusId(rsKeys.getInt(1));

			rsKeys.close();
		}catch(SQLException e){
			log.error("<<<jkpInsertStatus Error while inserting jkp_status", e);
		}
		log.info("<<< jkpInsertStatus");
		return jkpStatus;
	}
	
	/**
	 * Return last succeed jkp_status.jkp_id if exists, if not return null
	 * @param jkpFileIdentification
	 * @return
	 */
	public Integer jkpIdImported(String jkpFileIdentification){
		String sqlStatus = "SELECT f.jkp_id  AS jkp_id FROM jkp_file AS f JOIN jkp_status AS stt ON f.jkp_id = stt.jkp_id "
				+ "WHERE `status` = ? AND f.jkp_identification = ?";
		try(PreparedStatement stmt = con.prepareStatement(sqlStatus, PreparedStatement.RETURN_GENERATED_KEYS)){
			stmt.setString(1, JKPFileStatus.IMPORTED);
			stmt.setString(2, jkpFileIdentification);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				return rs.getInt("jkp_id");
			}
		}catch(SQLException e){
			log.error("<<< isJkpStatusImported", e);
		}
		return null;
	}
	
}

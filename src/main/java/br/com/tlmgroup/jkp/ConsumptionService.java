package br.com.tlmgroup.jkp;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MjkpaType;
import javax.ws.rs.core.Response;

import br.com.tlmgroup.commons.client.odin.BusinessAutomationClient;
import br.com.tlmgroup.commons.client.utils.UtilsClient;
import br.com.tlmgroup.jkp.bean.ExportResponse;
import br.com.tlmgroup.jkp.bean.ImportResponse;
import br.com.tlmgroup.jkp.bean.ProcessResponse;
import br.com.tlmgroup.jkp.process.Export;
import br.com.tlmgroup.jkp.process.Import;
import br.com.tlmgroup.jkp.process.Nid;

@Path("/")
public class ConsumptionService extends Service{
		
	UtilsClient utilsClient = new UtilsClient();
	BusinessAutomationClient baClient = new BusinessAutomationClient();
	Nid odin = new Nid();

	/**
	 * Imports the JKP files from FTP setted in JKP_IMPORT entry on Environment.kdbx file to
	 * Local database eletronic_data_interchange
	 * - deletes the original files from FTP
	 * - imports the files
	 * - rejects files with wrong patters (all rows are not imported)
	 * - send notifications if the file as not imported
	 * 
	 * JKP file have this patterns: 
	 * 20181227132650_e9cf97f063f3d4ed7a4faee0c064e3da.jkp 
	 * 20181227132650                      = Create date of file (used as KEY of file)
	 * e9cf97f063f3d4ed7a4faee0c064e3da    = MD5SUM of file
	 * .jkp                                = Obligatory Extension (.jkp)
	 * 
	 * @query import_from_ftp (y) if set as "n" avoid the import of files from FTP 
	 * @query delete_ftp_files (y) if set as "n" not delete the JKP files on FTP server 				
	 * @query send_import_error_notification (y) is set as "n" not send the error notification on import process 
	 * @return  Json with a summary of import process
	 */
	@GET
	@Path("/import-from-file")
	@Produces(MjkpaType.APPLICATION_JSON)
	public Response importFromFile(
			@DefaultValue(Import.IMPORT_FILES_FROM_FTP) @QueryParam("import_from_ftp") String importFromFtp,
			@DefaultValue(Import.DELETE_FILES_FTP) @QueryParam("delete_ftp_files") String deleteFtpFiles,
			@DefaultValue(Import.SEND_IMPORT_ERROR_NOTIFICATIONS) @QueryParam("send_import_error_notification") String sendImportNotification
			){
		Import importer = new Import();
		ImportResponse importResponse;
		try {
			importResponse = importer.importProcess(importFromFtp, deleteFtpFiles, sendImportNotification);
			return successResponse("ImportResponse", importResponse);
		} catch (Exception e) {
			return errorResponse("import-from-file" + e.getMessage());
		}
	}
	
	/**
	 * Exports from database all rows in eletronic_data_interchange.consumption where is_sent is NULL or
	 * is_sent = "0000-00-00 00:00:00" summarizing by subscription.
	 * 
	 * If subscription is not Active, don't have the JKP resources or happen some process error, the registers will be  
	 * marked as sent (is_sent = NOW()) and the data will be recorded in contention table.
	 * 
	 * If everything goes well the register will be send to API IncrementResourceUsage_API
	 * 
	 * @query send_export_error_notification (y) is set as "n" not send the error notification on export process
	 * @return Json with a summary of export process
	 */
	@GET
	@Path("/export-to-odin")
	@Produces(MjkpaType.APPLICATION_JSON)
	public Response exportToNid(
			@DefaultValue(Export.SEND_EXPORT_ERROR_NOTIFICATIONS) @QueryParam("send_export_error_notification") String sendExportNotification
			){
		Export exporter = new Export();
		ExportResponse exportResponse;
		try {
			exportResponse = exporter.exportProcess(sendExportNotification);
			return successResponse("ExportResponse", exportResponse);
		} catch (Exception e) {
			return errorResponse("export-to-odin" + e.getMessage());
		}
	}

	
	/**
	 * Executes: /import-from-file 
	 *           /export-to-odin
	 *           
	 * 
	 * @query import_from_ftp (y) if set as "n" avoid the import of files from FTP 
	 * @query delete_ftp_files (y) if set as "n" not delete the JKP files on FTP server 				
	 * @query send_import_error_notification (y) is set as "n" not send the error notification on import process
	 * @query send_export_error_notification (y) is set as "n" not send the error notification on export process
	 * @return  Json with a summary of import/export process
	 */
	@GET
	@Path("/process")
	@Produces(MjkpaType.APPLICATION_JSON)
	public Response importAndExportProcess(
			@DefaultValue(Import.IMPORT_FILES_FROM_FTP) @QueryParam("import_from_ftp") String importFromFtp,
			@DefaultValue(Import.DELETE_FILES_FTP) @QueryParam("delete_ftp_files") String deleteFtpFiles,
			@DefaultValue(Import.SEND_IMPORT_ERROR_NOTIFICATIONS) @QueryParam("send_import_error_notification") String sendImportNotification,
			@DefaultValue(Export.SEND_EXPORT_ERROR_NOTIFICATIONS) @QueryParam("send_export_error_notification") String sendExportNotification){
		
		Import importer = new Import();
		Export exporter = new Export();
		ProcessResponse processResponse = new ProcessResponse();
		
		try{
			processResponse.setImportResponse(importer.importProcess(importFromFtp, deleteFtpFiles, sendImportNotification));
			processResponse.setExportResponse(exporter.exportProcess(sendExportNotification));
		}catch(Exception e){
			return errorResponse("jkp-process: " + e.getMessage());
		}
		
		return successResponse("jkp-process", processResponse);

	}

	
}

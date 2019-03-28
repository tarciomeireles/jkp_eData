# JKP - Eletronic Data Interchange


#### GET /import-from-file

Imports the JKP files from FTP setted in JKP_IMPORT entry on env.chipher file to Local database 

- deletes the original files from FTP
- imports the files
- rejects files with wrong patters (all rows are not imported)
- send notifications if the file as not imported

JKP file have this patterns: 

- 20181227132650_e9cf97f063f3d4ed7a4faee0c064e3da.jkp 
- 20181227132650  - Create date of file (used as KEY of file)
- e9cf97f063f3d4ed7a4faee0c064e3da - MD5SUM of file
- .jkp  - Obligatory Extension (.jkp)

<br />@query import_from_ftp (y) if set as "n" avoid the import of files from FTP 
<br />@query delete_ftp_files (y) if set as "n" not delete the JKP files on FTP server @query send_import_error_notification (y) is set as "n" not send the error notification on import process 
<br />@return  Json with a summary of import process

```mermaid
graph LR;
	Login((Login)) --> GetFtpFiles
		GetFtpFiles --> DeleteFilesFromRemotePath
		DeleteFilesFromRemotePath --> VerifyJKPFiles
		VerifyJKPFiles{VerifyJKPFiles}
			VerifyJKPFiles-->|Valid|ImportFilesToDatabase
			VerifyJKPFiles-->|invalid|Rollback
		ImportFilesToDatabase-->MoveFileToImported
		Rollback-->MoveFileToRejected
		MoveFileToImported-->SuccessResponse
		MoveFileToRejected-->SuccessResponse
	SuccessResponse((SuccessResponse))
```

#### GET /export-to-odin
Exports from database all rows in eletronic_data_interchange.consumption where is_sent is NULL or is_sent = "0000-00-00 00:00:00" summarizing by subscription.

If subscription is not Active, don't have the JKP resources or happen some process error, the registers will be marked as sent (is_sent = NOW()) and the data will be recorded in contention table. If everything goes well the register will be send to API IncrementResourceUsage_API

<br />@query send_export_error_notification (y) is set as "n" not send the error notification on export process
<br />@return Json with a summary of export process

```mermaid
graph LR;
	Begin((Begin)) --> SelectResourcesSumBySubscr
		SelectResourcesSumBySubscr --> ValidSubscription
		ValidSubscription{ValidSubscription}
			ValidSubscription-->|Valid|ValidJKPSubscription
			ValidSubscription-->|Invalid|SendToContention
		ValidJKPSubscription{ValidJKPSubscription}
			ValidJKPSubscription-->|hasJKPResources|ExportToNid
			ValidJKPSubscription-->|noJKPResources|SendToContention
		ExportToNid-->VerifyNidResponse
		VerifyNidResponse{VerifyNidResponse}
			VerifyNidResponse-->Ok
			VerifyNidResponse-->NotOk
		Ok-->ResumeResponse
		NotOk-->SendToContention
		ResumeResponse((ResumeResponse))
		SendToContention-->ResumeResponse
```	


#### GET /process

Executes: /import-from-file and /export-to-odin

<br />@query import_from_ftp (y) if set as "n" avoid the import of files from FTP 
<br />@query delete_ftp_files (y) if set as "n" not delete the JKP files on FTP server 				
<br />@query send_import_error_notification (y) is set as "n" not send the error notification on import process
<br />@query send_export_error_notification (y) is set as "n" not send the error notification on export process
<br />@return  Json with a summary of import/export process

```mermaid
graph LR;
	Begin((Begin)) --> ImportFromFile
		ImportFromFile --> ExportToNid
		ExportToNid --> ResumeResponse
	ResumeResponse((ResumeResponse))
```	


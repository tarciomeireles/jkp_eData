package br.com.tlmgroup.jkp.helper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import br.com.tlmgroup.jkp.bean.JKPDataImport;
import br.com.tlmgroup.jkp.bean.JKPFileStatus;

import com.opencsv.CSVReader;

public class JKPCsvReader extends AbstractUniversal {
	File file = null;
	String[] nextLine;
	Integer lineNumber = 0;
	Long size = 0L;
	CSVReader reader = null;
	JKPDataImport jkpData = new JKPDataImport();
	private static final int INDEX_DATE 		= 0;
	private static final int INDEX_SUBSCRIPTION = 1;
	private static final int INDEX_MAILBOX 		= 2;
	private static final int INDEX_TX 			= 3;
	private static final int INDEX_RX 			= 4;
	private static final int INDEX_NOT 			= 5;
	private static final String DATE_FORMAT = "dd/mm/yyyy";
	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	
	
	public JKPCsvReader(String filepath){
		this.file = new File(filepath);
	}
	
	/**
	 * Get the identification of file
	 * Example: 20181213132650_53f46797c25317a0d19635a39a3d8252.jkp
	 * returns: 20181213132650
	 * @return String 
	 */
	public String getJkpFileIdentification(){
		String[] split = getName().split("_");
		return split[0];
	}
	
	/**
	 * Get the declared md5 of file (NOT THE REAL MD5)
	 * Example: 20181213132650_53f46797c25317a0d19635a39a3d8252.jkp
	 * returns: 53f46797c25317a0d19635a39a3d8252
	 * @return String 
	 */
	public String getDeclaredMD5FromFilename(){
		String[] splitUnderline = getName().split("_");
		String[] splitPoint = splitUnderline[1].split(".");
		return splitPoint[0];
	}
	
	/**
	 * Open a csv file to be read and put the reader in the field "reader"
	 * @return Reader of csvfile
	 */
	public CSVReader open(){
		log.info(">>> JKPCsvReader.open");
		
		try{
			reader = new CSVReader(new FileReader(this.file.getAbsoluteFile()), ';');
			size = file.length();
			
		}catch(IOException e){
			log.error("<<< JKPCsvReader.open: Unable to open file.", e);
		}
		log.info("<<< JKPCsvReader.open");
		return reader;
	}
	
	/**
	 * Closes the CSV reader
	 */
	public void close(){
		log.info(">>> JKPCsvReader.close Closing JKP File");
		try {
			reader.close();
		} catch (IOException e) {
			log.error("<<< JKPCsvReader.close: Unable to close file.", e);
		}
		log.info("<<< JKPCsvReader.close Closing JKP File");
	}
	
	/**
	 * Get a line of CSV file and increment the cursor to next row
	 * @return A populated JKPDataImport or null
	 * @throws IOException
	 * @throws Exception
	 */
	public JKPDataImport read() throws IOException, Exception{		
		if ((nextLine = reader.readNext()) != null) {
			lineNumber++;
			if(isValid(nextLine))
				jkpData = this.setJKPData(nextLine);
			else
				throw new Exception("JKPFile:" + this.getClass() + " line:" + lineNumber + " is invalid");
		}else{
			return null; 
		}
		
		return jkpData; 
	}
	

	/**
	 * Return true or false to a valid or invalid csv JKP line
	 * @param jkpLine
	 * @return bool
	 */
	private boolean isValid(String [] jkpLine){
		
		final String errorMessage = "<<< JKPCsvReader.isValid: ";
		
		try{
			dateFormat.parse(jkpLine[INDEX_DATE]);
			Integer.parseInt(jkpLine[INDEX_SUBSCRIPTION]);
			Integer.parseInt(castEmptyToZero(jkpLine[INDEX_TX]));
			Integer.parseInt(castEmptyToZero(jkpLine[INDEX_RX]));
			Integer.parseInt(castEmptyToZero(jkpLine[INDEX_NOT]));			
		}catch(NumberFormatException nfe){
			log.error(errorMessage + nfe.getMessage());
			return false;
		}catch(ParseException pe) {
			log.error(errorMessage + pe.getMessage());
			return false;
		}catch(NullPointerException e){
			log.error(errorMessage + e.getMessage());
			return false;
		}
		
		return true;		
	}
	
	private String castEmptyToZero(String inputString) {
		return (inputString.isEmpty()) ? "0" : inputString;
	}
	
	/**
	 * Set JKP Data by each INDEX of CSV line string
	 * @param jkpLine
	 * @return JKPDataImport
	 */
	private JKPDataImport setJKPData(String [] jkpLine){
		
		try {
			jkpData.setConsumptionDate(dateFormat.parse(jkpLine[INDEX_DATE]));
			jkpData.setSubscriptionId(Integer.parseInt(jkpLine[INDEX_SUBSCRIPTION]));
			jkpData.setMailboxId(jkpLine[INDEX_MAILBOX]);
			jkpData.setConsumptionTx(Integer.parseInt(castEmptyToZero(jkpLine[INDEX_TX])));
			jkpData.setConsumptionRx(Integer.parseInt(castEmptyToZero(jkpLine[INDEX_RX])));
			jkpData.setConsumptionNot(Integer.parseInt(castEmptyToZero(jkpLine[INDEX_NOT])));
		} catch (ParseException e) {
			log.error("<<< Cannot cast " + jkpLine[INDEX_DATE] + " into date");
		}
		
		return jkpData;
	}
	
	/**
	 * Returns the status (name, md5, size...) of a JKP CSV file
	 * @return
	 */
	public JKPFileStatus getStatus(){
		JKPFileStatus jkpStatus =  new JKPFileStatus();
		jkpStatus.setMd5(this.md5());
		jkpStatus.setFilename(this.getName());
		jkpStatus.setLines(this.getActualLine());
		jkpStatus.setSize(size);
		return jkpStatus;
	}
	
	/**
	 * Returns the current line number of file.
	 * @return
	 */
	public Integer getActualLine(){
		return lineNumber;
	}
	
	/**
	 * Returns the filename
	 * @return example: 20181213132650_53f46797c25317a0d19635a39a3d8252.jkp
	 */
	public String getName(){
		return this.file.getName();
	}

	/**
	 * Returns the md5 hash of JKP csv file
	 * @return example 53f46797c25317a0d19635a39a3d8252
	 */
	private String md5(){
		String md5 = null;
		try (InputStream is = Files.newInputStream(Paths.get(this.file.getAbsolutePath()))) {
		    md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is).toLowerCase();
		}catch(IOException e){
			log.error("<<< JKPCsvReader.md5: Unable to get MD5 from file: " + this.file.getAbsolutePath(), e);
		}
		return md5;
	}
	
	/**
	 * Verify if MD5 on filename is valid with the MD5 of file
	 * @return boolean
	 */
	public Boolean fileNameMD5isValid(){
		String md5 = this.md5();
		String declaredMd5 = this.getDeclaredMD5FromFilename();
		return (declaredMd5.equals(md5));
	}
}

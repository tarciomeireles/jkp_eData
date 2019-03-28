package br.com.tlmgroup.jkp.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Filehandler extends AbstractUniversal{
	
	public static final String PATH_IMPORTED = "imported";
	public static final String PATH_REJECTED = "rejected";
	public static final String JKP_FILENAME_PATTERN = "[0-9]{14}+\\_+[a-f0-9]{32}+\\.jkp";
	
	
	
	/**
	 * List all JKP files in a path using JKP_FILENAME_PATTERN to identify them.
	 * @param localPath
	 * @return
	 */
	public List<File> listJkpFiles(String localPath){
		log.info(">>> listJkpFiles - path:" + localPath);
		List<File> files = new ArrayList<>();
		try {
			if(new File(localPath).exists() && Files.list(Paths.get(localPath)).filter(Files::isRegularFile).count() >= 1){
	    		Files.list(Paths.get(localPath))
	    		.filter(Files::isRegularFile)
	    		.filter(p -> p.getFileName().toString().toLowerCase().matches(JKP_FILENAME_PATTERN.toLowerCase()))
	    		.forEach( f -> files.add(f.toFile()));
			} else {
				log.info("There are no JKP files to be processed on: " + localPath);
			}
		} catch (IOException e1) {
			log.error("Error on getting JKP files from folder: "+ localPath + e1.getMessage(), e1);
		}
		log.info("<<< listJkpFiles - path:" + localPath + " - Found files:" + files.size());
		return files;
	}
	
	/**
	 * Moves a File to destination
	 * @param file
	 * @param destination
	 * @return
	 */
	private boolean moveFile(File file, String destination){
		if(file.renameTo(new File(destination))){
			log.info("Moving " + file.getAbsolutePath() + " to " + destination);
			return true;
		}else{
			log.error("Cannot move " + file.getAbsolutePath() + " to " + destination);
			return false;
		}
	}
	
	
	/**
	 * Returns a String with the current DateTime in format yyyyMMddHHmmss
	 * Example: 20190114085548
	 * @return
	 */
	public String now(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");  
	    Date date = new Date();  
	    return formatter.format(date);
	}
	
	/**
	 * Creates a directory at 'pathname' returns true if the directory was created
	 * @param pathname
	 * @return
	 */
	private boolean mkdir(String pathname){
		File f = new File(pathname);
		if(f.mkdirs()){
			log.info("Directory " + pathname + " created");
			return true;
		}
		return false;
	}
	
	
	/**
	 * Moves a JKP file from JKP's path to subfolder in PATH_IMPORTED
	 * Creates the subfolder PATH_IMPORTED if not exists 
	 * Example:
	 * 
	 *    /tmp/jkp/file.jkp -> /tmp/jkp/imported/file.jkp
	 * @param file
	 * @return
	 */
	public boolean moveToImported(File file){
		String destinationPath = file.getParent() + File.separator + PATH_IMPORTED;
		String destinationFile = destinationPath +  File.separator + file.getName() + "_" + this.now();
		this.mkdir(destinationPath);
		return this.moveFile(file, destinationFile);
	}
	
	/**
	 * Moves a JKP file from JKP's path to subfolder in PATH_REJECTED
	 * Creates the subfolder PATH_REJECTED if not exists 
	 * Example:
	 * 
	 *    /tmp/jkp/file.jkp -> /tmp/rejected/imported/file.jkp
	 * @param file
	 * @return
	 */
	public boolean moveToRejected(File file){
		String destinationPath = file.getParent() + File.separator + PATH_REJECTED;
		String destinationFile = destinationPath +  File.separator + file.getName() + "_" + this.now();
		this.mkdir(destinationPath);
		return this.moveFile(file, destinationFile);
	}
}

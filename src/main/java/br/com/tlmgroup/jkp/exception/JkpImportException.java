package br.com.tlmgroup.jkp.exception;

@SuppressWarnings("serial")
public class JkpImportException extends Exception {
	final String  message;
	
	public JkpImportException(String message){
		this.message = message;
	}
	
	@Override
	public String toString(){
		return "JkpImportException: " + message;
	}
}

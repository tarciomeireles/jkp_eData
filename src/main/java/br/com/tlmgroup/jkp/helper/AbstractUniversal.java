package br.com.tlmgroup.jkp.helper;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractUniversal {
	protected static final Log log = LogFactory.getLog(Class.class.getName());
	
	public String toString() {
		   return ">>>_toString: " + ToStringBuilder.reflectionToString(this) ;
	}
	
	public Config cfg(){
		return new Config();
	}
	
}
package br.com.tlmgroup.jkp;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MjkpaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.tlmgroup.jkp.helper.AbstractUniversal;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Service extends AbstractUniversal{
	
	private static final Log log = LogFactory.getLog(Service.class);
	protected Gson gson;
	
	/**
	 * Constant tags
	 */
	protected static final String ERROR    = "error";
	protected static final String FAULT    = "fault";
	protected static final String MESSAGE  = "message";
	protected static final String RESULT   = "result";
	protected static final String MJKPA_TYPE_CHARSET = MjkpaType.APPLICATION_JSON + "; charset=UTF-8";

	public Service() {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);  
		gsonBuilder.serializeNulls(); 
		this.gson = gsonBuilder.create();
	}
	
	protected Response errorResponse(String faultMessage){
		log.error(faultMessage);
		
		Map<String, Object> map = new HashMap<>();
		map.put(ERROR, true);
		map.put(FAULT, faultMessage);
		map.put(RESULT, new Object());
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(map)).type(MJKPA_TYPE_CHARSET).build();
	}
	
	protected Response successResponse(String rootName, Object response, String message){

		Map<String, Object> responseMap = new HashMap<>();
		
		responseMap.put(rootName, response);
		
		Map<String, Object> map = new HashMap<>();
		map.put(ERROR, false);
		map.put(MESSAGE, message);
		map.put(RESULT, responseMap);
		return Response.ok().entity(gson.toJson(map)).type(MJKPA_TYPE_CHARSET).build();
	}
	
	protected Response successResponse(String rootName, Object response) {
		return successResponse(rootName, response, "");
	}
	
	protected Response successResponseStatus(Object response){
		Map<String, Object> map = new HashMap<>();
		map.put(ERROR, false);
		map.put(FAULT, "");
		map.put(RESULT, response);
		return Response.ok().entity(gson.toJson(map)).type(MJKPA_TYPE_CHARSET).build();
	}
}
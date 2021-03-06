package com.example;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.spi.PropertyDefiner;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class JSONFormatterLB extends LayoutBase<ILoggingEvent> implements PropertyDefiner{
	
	private static final JsonFactory FACTORY = new MappingJsonFactory();
	private String appName;
	

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	@Override
	public String getPropertyValue() {
		return null;
	}

	private ThreadLocal<DateFormat> formatter = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new ISO8601DateFormat();
		}
	};
	
	@Override
	public String doLayout(ILoggingEvent event) {
		return format(event);
	}

	
	public String format(ILoggingEvent event) {
		try {
			return formatAsJson(event);
		} catch(Exception ex) {
			// this really should never happen, but we can't throw an
			// exception so we'll just print the log class in JSON
			// format.
			return "{ \"logfailure\":\"" + ex.getClass().toString() + "\"}";
		}
	}
	
	private String formatAsJson(ILoggingEvent event) throws IOException {
		StringWriter writer = new StringWriter();
		formatAsJson(writer, event);
		
		return writer.toString();
	}
	
	private StringWriter formatAsJson(StringWriter writer, ILoggingEvent event) throws IOException {
		JsonGenerator json = FACTORY.createGenerator(writer);
		
		json.writeStartObject();
		if (appName != null){
			json.writeStringField("appName", appName);
		}
		json.writeStringField("name", event.getLoggerName());
		json.writeNumberField("time", event.getTimeStamp());
		
		Date date = new Date(event.getTimeStamp());
		json.writeStringField("date", formatter.get().format(date));
		json.writeStringField("level", event.getLevel().toString());
		json.writeStringField("thread", event.getThreadName());
		json.writeStringField("message", event.getFormattedMessage());
		
		Map<String, String> context = event.getMDCPropertyMap();
		if(context != null && !context.isEmpty()) {
			for(Map.Entry<String, String> entry : context.entrySet()) {
				json.writeStringField(entry.getKey().toLowerCase(), entry.getValue().toString());
			}
		}
		
/*	These do not exist for IloggingEvent. MAy have to use PatternLayoutEncoder for this.  
			json.writeStringField("class", event.getClass().toString());
			json.writeStringField("method", event.getMethod());
			json.writeStringField("line", event.getLine));
*/		

		IThrowableProxy tp = event.getThrowableProxy();
		if (tp != null) {
			String fullStack = new String();
			
			String className = tp.getClassName();
			String message = tp.getMessage();
			
			fullStack +=  className +": " + message + "\n";
			
			while (tp.getCause() != null){
			tp = tp.getCause();
			
			className = tp.getClassName();
			message = tp.getMessage();
			
			fullStack +=  className +": " + message + "\n";
			}	
	
			json.writeStringField("stack", fullStack);
		
		}
		
		json.writeEndObject();
		json.writeRaw(CoreConstants.LINE_SEPARATOR);
		json.flush();
		json.close();
		
		return writer;
	}



}

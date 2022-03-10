package dev.anarchy.translate.type.velocity;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.tools.generic.ComparisonDateTool;
import org.apache.velocity.tools.generic.ConversionTool;
import org.apache.velocity.tools.generic.DateTool;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import dev.anarchy.common.dto.Document;
import dev.anarchy.translate.util.Base64Util;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.translate.util.TranslateServiceInterface;

public class VelocityTranslateService implements TranslateServiceInterface {

	private static final String DOCUMENT = "document";
	
	private static final Properties VELOCITY_PROPERTIES;
	
	private static final VelocityEngine VELOCITY_ENGINE;
	
	private static final StringWriter stringWriter;
	
	private static final RuntimeServices runtimeServices;
	
	static {
		VELOCITY_PROPERTIES = new Properties();
		VELOCITY_PROPERTIES.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");
		
		VELOCITY_ENGINE = new VelocityEngine();
		VELOCITY_ENGINE.init(VELOCITY_PROPERTIES);
		
		stringWriter = new StringWriter();

        runtimeServices = RuntimeSingleton.getRuntimeServices();
	}

	public String translate(String templateContent, String dataModel) throws ParseException {
        
        // Convert datamodel into json
        JSONObject jsonModel = (JSONObject) JSONValue.parse(dataModel);
        
        // Convert DataModel into VelocityContext
        VelocityContext context = buildContext(jsonModel);

        // Create Template
        Template template = createTemplate(templateContent);
        
        // Run template
        stringWriter.flush();
        template.merge(context, stringWriter);
        return stringWriter.toString();
	}

	private Template createTemplate(String templateContent) throws ParseException {
        SimpleNode node = runtimeServices.parse(templateContent, "StringTemplate");
        Template template = new Template();
        template.setRuntimeServices(runtimeServices);
        template.setData(node);
        template.initDocument();
        
        return template;
	}

	@SuppressWarnings("unchecked")
	private VelocityContext buildContext(JSONObject jsonModel) {
        VelocityContext context = new VelocityContext();

        // Incoming document object
        if ( jsonModel != null )
        	context.put(DOCUMENT, new Document(jsonModel));
        
        // Custom utils
        context.put(JSONUtils.class.getSimpleName(), new JSONUtils());
        context.put(Base64Util.class.getSimpleName(), new Base64Util());
        
        // Base java Math
	    context.put(Integer.class.getSimpleName(), Integer.class);
	    context.put(Double.class.getSimpleName(), Double.class);
	    context.put(Boolean.class.getSimpleName(), Boolean.class);
	    context.put(Float.class.getSimpleName(), Float.class);
	    context.put(Math.class.getSimpleName(), Math.class);
        
        // Velocity Tools
        context.put(DateTool.class.getSimpleName(), new ComparisonDateTool());
        context.put(ConversionTool.class.getSimpleName(), new ConversionTool());
        context.put(StringUtils.class.getSimpleName(), new StringUtils());
        
        return context;
	}

}

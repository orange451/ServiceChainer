package dev.anarchy.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.common.condition.ConditionMeta;
import dev.anarchy.common.util.RouteHelper;
import dev.anarchy.translate.util.ServiceChainHelper;
import freemarker.template.TemplateException;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class DServiceDefinition extends DRouteElement implements DConditionMetaElement {
	
	@JsonProperty("ExtensionhandlerRouteId")
	private String routeId;
	
	@JsonProperty("TransformationType")
	private String transformationType;
	
	@JsonProperty("TemplateContent")
	private String templateContent;
	
	@JsonProperty("DestinationParams")
	private List<DDestinationParams> destinationParams;
	
	@JsonProperty("AugmentPayload")
	private String augmentPayload;
	
	@JsonProperty("Condition")
	private String condition;
	
	@JsonProperty("Sequence")
	private int sequence;
	
	@JsonProperty("_ConditionMeta")
	private ConditionMeta conditionMeta;
	
	/** Metadata :: Last known user-supplied response from service definition **/
	@JsonProperty("_MockResponse")
	private String mockResponse;

	/** Metadata :: Last known user-supplied json to test against **/
	@JsonProperty("_LastInput")
	private String lastInput;
	
	public DServiceDefinition() {
		super();
		this.setPosition(ServiceChainHelper.getDefaultServiceDefinitionX(), ServiceChainHelper.getDefaultServiceDefinitionY());
		this.setSize(ServiceChainHelper.getDefaultServiceDefinitionWidth(), ServiceChainHelper.getDefaultServiceDefinitionHeight());
		this.setColor(ServiceChainHelper.getDefaultServiceDefinitionColor());
	}

	@JsonIgnore()
	public void setExtensionHandlerRouteId(String routeId) {
		this.routeId = routeId;
		this.onChangedEvent.fire();
	}

	@JsonProperty("ExtensionhandlerRouteId")
	public String getExtensionHandlerRouteId() {
		return this.routeId;
	}

	@JsonIgnore()
	public String getTransformationType() {
		return transformationType;
	}

	@JsonIgnore()
	public void setTransformationType(String transformationType) {
		if ( StringUtils.equals(transformationType, this.transformationType) )
			return;
		
		this.transformationType = transformationType;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getTemplateContent() {
		return templateContent;
	}

	@JsonIgnore()
	public void setTemplateContent(String templateContent) {
		if ( StringUtils.equals(templateContent, this.templateContent) )
			return;
		
		this.templateContent = templateContent;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public List<DDestinationParams> getDestinationParams() {
		return destinationParams;
	}

	@JsonIgnore()
	public void setDestinationParams(List<DDestinationParams> destinationParams) {
		this.destinationParams = destinationParams;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getAugmentPayload() {
		return augmentPayload;
	}

	@JsonIgnore()
	public void setAugmentPayload(String augmentPayload) {
		if ( StringUtils.equals(augmentPayload, this.augmentPayload) )
			return;
		
		this.augmentPayload = augmentPayload;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getCondition() {
		return condition;
	}

	@JsonIgnore()
	public void setCondition(String condition) {
		if ( StringUtils.equals(condition, this.condition) )
			return;
		
		this.condition = condition;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getMockResponse() {
		return mockResponse;
	}

	@JsonIgnore()
	public void setMockResponse(String mockResponse) {
		if ( StringUtils.equals(mockResponse, this.mockResponse) )
			return;
		
		this.mockResponse = mockResponse;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public ConditionMeta getConditionMeta() {
		return conditionMeta;
	}

	@JsonIgnore()
	public void setConditionMeta(ConditionMeta conditionMeta) {
		this.conditionMeta = conditionMeta;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getLastInput() {
		return lastInput;
	}

	@JsonIgnore()
	public void setLastInput(String lastInput) {
		this.lastInput = lastInput;
	}

	@JsonIgnore()
	public int getSequence() {
		return sequence;
	}

	@JsonIgnore()
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@JsonIgnore
	@Override
	public Map<String, Object> transform(Map<String, Object> inputPayload) throws ParseException, IOException, TemplateException {
		// Try to transform the data
		if ( !StringUtils.isEmpty(this.getTemplateContent()) && !StringUtils.isEmpty(this.getTransformationType()) ) {
			return RouteHelper.transform(this, inputPayload);
		} else {
			return inputPayload;
		}
	}

	@JsonIgnore()
	@Override
	public DServiceDefinition clone() {
		DServiceDefinition newInstance = (DServiceDefinition) super.clone();
		
		newInstance.routeId = routeId;
		newInstance.transformationType = transformationType;
		newInstance.templateContent = templateContent;
		newInstance.augmentPayload = augmentPayload;
		newInstance.condition = condition;
		newInstance.mockResponse = mockResponse;
		newInstance.lastInput = lastInput;
		
		for (DDestinationParams params : destinationParams)
			newInstance.destinationParams.add(params.clone());
		
		return newInstance;
	}
}

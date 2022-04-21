package dev.anarchy.common;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.common.condition.ConditionMeta;
import freemarker.template.TemplateException;

public class DConditionElement extends DRouteElement implements DConditionMetaElement {
	
	@JsonProperty("_ConditionMeta")
	private ConditionMeta conditionMeta;
	
	@JsonProperty("PassRouteId")
	private String passDestinationId;
	
	@JsonProperty("FailRouteId")
	private String failDestinationId;
	
	public DConditionElement() {
		this.conditionMeta = new ConditionMeta();
	}

	@Override
	public Map<String, Object> transform(Map<String, Object> inputPayload)
			throws ParseException, IOException, TemplateException {
		return null;
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
	public void setPassRouteId(String destination) {
		if ( StringUtils.equals(destination, this.passDestinationId) )
			return;
		
		this.passDestinationId = destination;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getPassRouteId() {
		return this.passDestinationId;
	}

	@JsonIgnore()
	public void setFailRouteId(String destinationId) {
		if ( StringUtils.equals(destinationId, this.failDestinationId) )
			return;
		
		this.failDestinationId = destinationId;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getFailRouteId() {
		return this.failDestinationId;
	}
}

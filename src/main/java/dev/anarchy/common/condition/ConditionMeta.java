package dev.anarchy.common.condition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.common.DGraphElement;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class ConditionMeta extends DGraphElement {

	@JsonProperty("Condition")
	private String condition;
	
	@JsonProperty("LastConditionPayload")
	private String lastConditionPayload;

	@JsonIgnore()
	public String getCondition() {
		return condition;
	}

	@JsonIgnore()
	public void setCondition(String condition) {
		this.condition = condition;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	public String getLastConditionPayload() {
		return lastConditionPayload;
	}

	@JsonIgnore()
	public void setLastConditionPayload(String lastConditionPayload) {
		this.lastConditionPayload = lastConditionPayload;
		this.onChangedEvent.fire();
	}
}

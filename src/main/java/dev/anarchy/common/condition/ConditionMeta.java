package dev.anarchy.common.condition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.anarchy.common.DGraphElement;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class ConditionMeta extends DGraphElement {
	
	@JsonProperty("Condition")
	private String condition;

	@JsonIgnore()
	public String getCondition() {
		return condition;
	}

	@JsonIgnore()
	public void setCondition(String condition) {
		this.condition = condition;
		this.onChangedEvent.fire();
	}
}

package dev.anarchy.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.anarchy.common.condition.ConditionMeta;

@JsonIgnoreProperties(ignoreUnknown = true) 
public interface DConditionMetaElement {
	@JsonIgnore()
	public ConditionMeta getConditionMeta();

	@JsonIgnore()
	public void setConditionMeta(ConditionMeta conditionMeta);
}

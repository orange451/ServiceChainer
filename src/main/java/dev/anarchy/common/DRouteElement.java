package dev.anarchy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonProcessingException;

import dev.anarchy.event.Event;
import dev.anarchy.translate.util.JSONUtils;

@JsonTypeInfo(use=Id.DEDUCTION)
@JsonSubTypes({@Type(DServiceDefinition.class),@Type(DConditionElement.class)})
public abstract class DRouteElement extends DGraphElement implements DRouteElementI {
	
	@JsonProperty("Source")
	private String source;
	
	@JsonProperty("SourceId")
	private String sourceId;
	
	@JsonProperty("Destination")
	private String destination;
	
	@JsonProperty("DestinationId")
	private String destinationId;
	
	@JsonProperty("ChildRoute")
	private List<DRouteElement> childRoutes;

	@JsonProperty("Condition")
	private String condition;

	@JsonProperty("IsSync")
	private String isSync;
	
	public DRouteElement() {
		this.setDesination("RouteElement");
		this.setDesinationId(UUID.randomUUID().toString());
		this.setIsSync("true");
		this.childRoutes = new ArrayList<>();
	}
	
	public Event getOnChangedEvent() {
		return this.onChangedEvent;
	}

	@JsonIgnore()
	public void setSource(String source) {
		if ( StringUtils.equals(source, this.source) )
			return;
		
		this.source = source;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	@Override
	public String getSource() {
		return this.source;
	}

	@JsonIgnore()
	public void setSourceId(String sourceId) {
		if ( StringUtils.equals(sourceId, this.sourceId) )
			return;
		
		this.sourceId = sourceId;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	@Override
	public String getSourceId() {
		return this.sourceId;
	}

	@JsonIgnore()
	public void setDesination(String destination) {
		if ( StringUtils.equals(destination, this.destination) )
			return;
		
		this.destination = destination;
		this.onChangedEvent.fire();
		this.setName(destination);
	}

	@JsonIgnore()
	@Override
	public String getDestination() {
		return this.destination;
	}

	@JsonIgnore()
	private void setDesinationId(String destinationId) {
		if ( StringUtils.equals(destinationId, this.destinationId) )
			return;
		
		this.destinationId = destinationId;
		this.onChangedEvent.fire();
	}

	@JsonIgnore()
	@Override
	public String getDestinationId() {
		return this.destinationId;
	}

	@JsonIgnore()
	public String getIsSync() {
		return isSync;
	}

	@JsonIgnore()
	public void setIsSync(String isSync) {
		if ( StringUtils.equals(isSync, this.isSync) )
			return;
		
		this.isSync = isSync;
		this.onChangedEvent.fire();
	}
	
	@JsonIgnore()
	public void setCondition(String condition) {
		if ( StringUtils.equals(condition, this.condition) )
			return;
		
		this.condition = condition;
		this.onChangedEvent.fire();
	}
	
	@JsonIgnore()
	public String getCondition() {
		return this.condition;
	}
	
	@JsonIgnore()
	public List<DRouteElement> getChildRoutesUnmodifyable() {
		DRouteElement[] arr = new DRouteElement[this.childRoutes.size()];
		for (int i = 0; i < childRoutes.size(); i++) {
			arr[i] = childRoutes.get(i);
		}
		return Arrays.asList(arr);
	}
	
	@JsonIgnore()
	public void addChildRoute(DRouteElement element) {
		this.childRoutes.add(element);
	}
	
	@JsonIgnore()
	public void removeChildRoute(DRouteElement element) {
		this.childRoutes.remove(element);
	}

	public DRouteElement clone() {
		try {
			String json = JSONUtils.objectToJSON(this);
			return JSONUtils.convertToObject(json, this.getClass());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

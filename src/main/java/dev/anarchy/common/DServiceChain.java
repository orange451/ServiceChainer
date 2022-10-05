package dev.anarchy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.event.Event;
import dev.anarchy.event.NameChangeEvent;
import dev.anarchy.translate.util.JSONUtils;
import dev.anarchy.translate.util.ServiceChainHelper;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class DServiceChain implements DFolderElement,DRouteElementI {
	
	@JsonProperty("ExtensionHandlerId")
	private String handlerId;

	@JsonProperty("ExtensionhandlerRoute")
	private List<DRouteElement> routes = new ArrayList<>();

	@JsonProperty("RegisteredExtensionPoints")
	private List<DExtensionPoint> extensionPoints = new ArrayList<>();
	
	@JsonProperty("ComponentId")
	private String componentId;

	@JsonProperty("_Produces")
	private String produces = "JSON";
	
	@JsonProperty("_Name")
	private String name;
	
	@JsonProperty("_X")
	private double x;
	
	@JsonProperty("_Y")
	private double y;

	@JsonProperty("_Width")
	private double width;

	@JsonProperty("_Height")
	private double height;

	@JsonProperty("_Color")
	private String color;

	@JsonProperty("_LastInput")
	private String lastInput;
	
	@JsonIgnore()
	private Event onChangedEvent = new Event();
	
	@JsonIgnore
	private Event onNameChangeEvent = new NameChangeEvent();
	
	@JsonIgnore
	private Event onHandlerIdChangeEvent = new NameChangeEvent();
	
	@JsonIgnore
	private Event onParentChangeEvent = new Event();
	
	@JsonIgnore
	private Event onRouteAddedEvent = new Event();
	
	@JsonIgnore
	private Event onRouteRemovedEvent = new Event();
	
	@JsonIgnore
	private DFolder parent;
	
	public DServiceChain() {
		this.setName(ServiceChainHelper.getDefaultServiceChainName());
		this.setHandlerId(ServiceChainHelper.getDefaultServiceChainHandlerId());
		this.setLastInput(ServiceChainHelper.getDefaultServiceChainLastInput());
		this.setColor(ServiceChainHelper.getDefaultServiceChainColor());
		this.setPosition(ServiceChainHelper.getDefaultServiceChainElementX(), ServiceChainHelper.getDefaultServiceChainElementY());
		this.setSize(ServiceChainHelper.getDefaultServiceChainElementWidth(), ServiceChainHelper.getDefaultServiceChainElementHeight());
		this.getRegisteredExtensionPoints().add(new DExtensionPoint());
	}

	@JsonIgnore
	public String getName() {
		return this.name;
	}

	@JsonIgnore
	public void setName(String name) {
		if ( StringUtils.equals(name, this.name) )
			return;
		
		this.name = name;
		
		if ( onNameChangeEvent != null )
			onNameChangeEvent.fire(name);
		
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	public String getComponentId() {
		return this.componentId;
	}

	@JsonIgnore
	public void setComponentId(String componentId) {
		if ( StringUtils.equals(componentId, this.componentId) )
			return;
		
		this.componentId = componentId;
		
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	public Event getOnNameChangeEvent() {
		return this.onNameChangeEvent;
	}

	@JsonIgnore
	public Event getOnHandlerIdChangeEvent() {
		return this.onHandlerIdChangeEvent;
	}

	@JsonIgnore
	public Event getOnParentChangeEvent() {
		return this.onParentChangeEvent;
	}

	@JsonIgnore
	public Event getOnRouteAddedEvent() {
		return this.onRouteAddedEvent;
	}

	@JsonIgnore	
	public Event getOnRouteRemovedEvent() {
		return this.onRouteRemovedEvent;
	}

	@JsonIgnore
	public void setParent(DFolder parent) {
		DFolder oldParent = this.parent;
		this.parent = parent;
		
		if ( onParentChangeEvent != null )
			onParentChangeEvent.fire(parent, oldParent);
		
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	public void addRoute(DRouteElement chain) {
		if ( this.routes.add(chain) ) {
			this.onRouteAddedEvent.fire(chain);
		}
		this.onChangedEvent.fire();
		
		chain.getOnChangedEvent().connect((args)->{
			this.onChangedEvent.fire(args);
		});
	}

	@JsonIgnore
	public void removeRoute(DRouteElement chain) {
		if ( this.routes.remove(chain) ) {
			this.onRouteRemovedEvent.fire(chain);
		}
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	public String getHandlerId() {
		return this.handlerId;
	}

	@JsonIgnore
	public void setHandlerId(String handlerId) {
		if ( StringUtils.equals(handlerId, this.handlerId) )
			return;
		
		this.handlerId = handlerId;
		
		if ( onHandlerIdChangeEvent != null )
			onHandlerIdChangeEvent.fire(handlerId);
		
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	public List<DRouteElementI> getRoutesUnmodifyable() {
		DRouteElement[] arr = new DRouteElement[this.routes.size()];
		for (int i = 0; i < routes.size(); i++) {
			arr[i] = routes.get(i);
		}
		return Arrays.asList(arr);
	}

	@JsonIgnore	
	public void setRoutes(List<DRouteElement> routes) {
		this.routes = routes;
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	public List<DExtensionPoint> getRegisteredExtensionPoints() {
		return this.extensionPoints;
	}

	@JsonIgnore
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	public double getWidth() {
		return this.width;
	}

	@JsonIgnore
	public double getHeight() {
		return this.height;
	}

	@JsonIgnore
	public double getX() {
		return this.x;
	}

	@JsonIgnore
	public double getY() {
		return this.y;
	}

	@JsonIgnore
	public void setColor(String hexString) {
		if ( StringUtils.equals(hexString, this.color) )
			return;
		
		this.color = hexString;
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	public String getColor() {
		return this.color;
	}

	@JsonIgnore
	public Event getOnChangedEvent() {
		return this.onChangedEvent;
	}

	@JsonIgnore()
	@Override
	public String getSource() {
		return "ON_EVENT";
	}

	@JsonIgnore()
	@Override
	public String getSourceId() {
		return "ON_EVENT";
	}

	@JsonIgnore()
	@Override
	public String getDestination() {
		return "ON_EVENT";
	}
	
	@JsonIgnore()
	@Override
	public String getDestinationId() {
		return "ON_EVENT";
	}

	@JsonIgnore()
	public String getLastInput() {
		return lastInput;
	}

	@JsonIgnore()
	public void setLastInput(String lastInput) {
		if ( StringUtils.equals(lastInput, this.lastInput) )
			return;
		
		this.lastInput = lastInput;
		this.onChangedEvent.fire();
	}

	@JsonIgnore
	@Override
	public Map<String, Object> transform(Map<String, Object> inputPayload) {
		return inputPayload;
	}
	
	/**
	 * Performs deep copy on DService Chain.
	 */
	@JsonIgnore
	@Override
	public DServiceChain clone() {
		try {
			String json = JSONUtils.objectToJSON(this);
			return JSONUtils.convertToObject(json, this.getClass());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@JsonIgnore
	public void copyFrom(DServiceChain serviceChain) {
		try {
			String json = JSONUtils.objectToJSON(serviceChain);
			new ObjectMapper().readerForUpdating(this).readValue(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@JsonIgnore
	public void delete() {
		DApp.get().delete(this);
	}
}

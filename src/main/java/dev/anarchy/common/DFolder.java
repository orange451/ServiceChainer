package dev.anarchy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import dev.anarchy.event.Event;
import dev.anarchy.event.NameChangeEvent;
import dev.anarchy.translate.util.JSONUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DFolder implements DFolderElement {
	@JsonProperty("_Name")
	private String name;

	@JsonProperty("ExtensionHandler")
	private List<DFolderElement> children = new ArrayList<>();

	@JsonProperty("_Deletable")
	private boolean deletable = true;

	@JsonIgnore
	private boolean archivable = true;

	@JsonIgnore
	private NameChangeEvent onNameChangeEvent = new NameChangeEvent();

	@JsonIgnore
	private Event onChildAddedEvent = new Event();

	@JsonIgnore
	private Event onChildRemovedEvent = new Event();
	
	@JsonIgnore
	private Event onParentChangeEvent = new Event();
	
	public DFolder() {
		this.setName("Folder");
	}

	@JsonIgnore
	public String getName() {
		return this.name;
	}

	@JsonIgnore
	public void setName(String name) {
		this.name = name;
		this.onNameChangeEvent.fire(name);
	}

	@JsonIgnore
	public void addChild(DFolderElement chain) {
		if ( this.children.add(chain) ) {
			this.onChildAddedEvent.fire(chain);
		}
	}

	@JsonIgnore
	public void removeChild(DFolderElement chain) {
		if ( this.children.remove(chain) ) {
			this.onChildRemovedEvent.fire(chain);
		}
	}

	@JsonIgnore
	public DFolderElement getChild(String name) {
		for (DFolderElement chain : this.children)
			if ( chain.getName().equals(name) )
				return chain;
		
		return null;
	}

	@JsonIgnore
	public List<DFolderElement> getChildrenUnmodifyable() {
		DFolderElement[] arr = new DFolderElement[this.children.size()];
		for (int i = 0; i < children.size(); i++) {
			arr[i] = children.get(i);
		}
		return Arrays.asList(arr);
	}

	@JsonIgnore
	public Event getOnChildAddedEvent() {
		return this.onChildAddedEvent;
	}

	@JsonIgnore
	public Event getOnChildRemovedEvent() {
		return this.onChildRemovedEvent;
	}

	@JsonIgnore
	public NameChangeEvent getOnNameChangeEvent() {
		return this.onNameChangeEvent;
	}

	@JsonIgnore
	public boolean isArchivable() {
		return this.archivable;
	}

	@JsonIgnore
	public void setArchivable(boolean archivable) {
		this.archivable = archivable;
	}

	@JsonIgnore
	public boolean isDeletable() {
		return this.deletable;
	}

	@JsonIgnore
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	@JsonIgnore
	public void delete() {
		if ( !this.isDeletable() )
			return;
		
		// We need to iterate this slightly oddly because otherwise we risk concurrent modification exception.
		for (int i = this.children.size()-1; i >= 0; i--) {
			if ( i >= this.children.size() )
				continue;
			
			DFolderElement chain = this.children.get(i);
			if ( chain == null )
				continue;
			
			if ( chain instanceof DFolder && ((DFolder) chain).isDeletable() )
				((DFolder)chain).delete();
			
			if ( chain instanceof DServiceChain )
				((DServiceChain)chain).delete();
		}
		
		DApp.get().delete(this);
	}
	
	/**
	 * Performs deep copy on DFolder.
	 */
	@Override
	public DFolder clone() {
		try {
			String json = JSONUtils.objectToJSON(this);
			return JSONUtils.convertToObject(json, this.getClass());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

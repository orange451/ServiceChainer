package dev.anarchy.common;

import dev.anarchy.event.Event;

public class DApp {
	
	private static DApp app;
	
	private Event onDelete;

	private DApp() {
		this.onDelete = new Event();
	}
	
	public static DApp get() {
		if ( app == null )
			app = new DApp();
		
		return app;
	}
	
	public Event getOnDeleteEvent() {
		return this.onDelete;
	}

	public void delete(DFolderElement object) {
		onDelete.fire(object);
	}
}

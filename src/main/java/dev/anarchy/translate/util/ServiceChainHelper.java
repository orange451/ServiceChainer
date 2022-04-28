package dev.anarchy.translate.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import dev.anarchy.common.DConditionElement;
import dev.anarchy.common.DRouteElement;
import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;
import dev.anarchy.common.DServiceDefinition;
import dev.anarchy.common.util.RouteHelper;

public class ServiceChainHelper {
	private static final double HORIZONTAL_ELEMENT_SPACE = 40;
	
	private static final double VERTICAL_ELEMENT_SPACE = 60;
	
	/**
	 * Attempt to re-set all the required metadata to display a service chain and its subsequent routes.
	 */
	public static void fixServiceChain(DServiceChain serviceChain) {
		// Setting some metadata
		serviceChain.setPosition(getDefaultServiceChainElementX(), getDefaultServiceChainElementY());
		serviceChain.setColor(getDefaultServiceChainColor());
		serviceChain.setLastInput(getDefaultServiceChainLastInput());
		serviceChain.setSize(getDefaultServiceChainElementWidth(), getDefaultServiceChainElementHeight());
		
		// Fix missing required fields
		if ( StringUtils.isEmpty(serviceChain.getName()) )
			serviceChain.setName(getDefaultServiceChainName());
		
		if ( StringUtils.isEmpty(serviceChain.getHandlerId()) )
			serviceChain.setHandlerId(getDefaultServiceChainHandlerId());
		
		// Apply Metadata to children
		for (DRouteElementI route : serviceChain.getRoutesUnmodifyable()) {
			if ( route instanceof DServiceDefinition ) {
				fixServiceDefinition((DServiceDefinition) route);
			}
			
			if ( route instanceof DConditionElement ) {
				fixCondition((DConditionElement) route);
			}
		}
		
		// Layout Children
		layout(serviceChain.getRoutesUnmodifyable(), serviceChain);
	}
	
	private static void layout(List<DRouteElementI> routes, DRouteElementI route) {
		List<DRouteElementI> childRoutes = RouteHelper.getLinkedTo(routes, route);
		double totalWidth = 0;
		double maxHeight = 0;
		
		// Compute size
		for (DRouteElementI element : childRoutes) {
			totalWidth += element.getWidth();
			maxHeight = Math.max(maxHeight, element.getHeight());
		}
		totalWidth += (childRoutes.size()-1) * HORIZONTAL_ELEMENT_SPACE;
		
		double centerX = route.getX() + route.getWidth()/2;
		double bottomY = route.getY() + route.getHeight();
		
		// Layout
		int pushWidth = 0;
		for (DRouteElementI element : childRoutes) {
			double xx = centerX - (totalWidth/2) + pushWidth;
			double yy = bottomY + VERTICAL_ELEMENT_SPACE;
			element.setPosition(xx, yy);
			
			layout(routes, element);
			pushWidth += element.getWidth() + HORIZONTAL_ELEMENT_SPACE;
		}
	}

	private static void fixServiceDefinition(DServiceDefinition serviceDefinition) {
		serviceDefinition.setPosition(getDefaultServiceDefinitionX(), getDefaultServiceDefinitionY());
		serviceDefinition.setSize(getDefaultServiceDefinitionWidth(), getDefaultServiceDefinitionHeight());
		serviceDefinition.setColor(getDefaultServiceDefinitionColor());
		if ( StringUtils.isEmpty(serviceDefinition.getName()) )
			serviceDefinition.setName(serviceDefinition.getDestination());
	}
	
	private static void fixCondition(DConditionElement condition) {
		condition.setColor(ServiceChainHelper.getDefaultConditionElementColor());
		condition.setSize(getDefaultConditionElementWidth(), getDefaultConditionElementHeight());
		condition.setPosition(getDefaultServiceDefinitionX(), getDefaultServiceDefinitionY());
		if ( StringUtils.isEmpty(condition.getName()) )
			condition.setName("Condition");
	}
	
	/**
	 * Returns the specified service chain after unpacking conditions in to their own elements.
	 */
	public static DServiceChain unpack(DServiceChain serviceChain) {
		List<DRouteElementI> routes = serviceChain.getRoutesUnmodifyable();
		unpack(serviceChain, routes, serviceChain, 0);
		
		return serviceChain;
	}
	
	/**
	 * Returns the specified service chain after unpacking conditions in to their own elements.
	 */
	private static void unpack(DServiceChain serviceChain, List<DRouteElementI> routes, DRouteElementI rootRoute, double yOff) {
		double SPACING = 40;
		
		List<DRouteElementI> availableNextRoutes = RouteHelper.getLinkedTo(routes, rootRoute);
		for (DRouteElementI currentElement : availableNextRoutes) {
			
			if (currentElement instanceof DServiceDefinition) {
				if (!StringUtils.isEmpty(((DServiceDefinition)currentElement).getCondition())) {
					DConditionElement condition = ServiceChainHelper.newCondition(serviceChain);
					condition.setPosition(currentElement.getX(), currentElement.getY());
					condition.setCondition(((DServiceDefinition) currentElement).getCondition());
					
					RouteHelper.linkRoutes(rootRoute, condition);
					RouteHelper.linkRoutes(condition, (DRouteElement) currentElement);
					
					RouteHelper.connectCondition(condition, (DRouteElement) currentElement, true);
					updateFailNode(routes, condition, rootRoute);
					
					((DServiceDefinition) currentElement).setCondition(null);
					yOff += condition.getHeight() + SPACING;
				} else {
					DConditionElement condition = null;
					for (DRouteElementI t : RouteHelper.getLinkedTo(serviceChain.getRoutesUnmodifyable(), rootRoute)) {
						if ( t == currentElement ) 
							continue;
						
						if ( t instanceof DConditionElement ) {
							condition = (DConditionElement) t;
							break;
						}
					}
					if ( condition != null )
						updateFailNode(routes, condition, rootRoute);
				}
			}

			currentElement.setPosition(currentElement.getX(), currentElement.getY() + yOff);
			
			unpack(serviceChain, routes, currentElement, yOff);
		}
	}
	

	/**
	 * Pack condition nodes in to destination node.
	 */
	public static void pack(DServiceChain serviceChain) {
		List<DRouteElement> newRoutes = new ArrayList<DRouteElement>();
		List<DRouteElementI> routes = serviceChain.getRoutesUnmodifyable();
		
		pack(newRoutes, routes, serviceChain, serviceChain);
		
		serviceChain.setRoutes(newRoutes);
	}

	/**
	 * Delete DConditionElement, and pack them in to child node
	 */
	public static void pack(List<DRouteElement> newRoutes, List<DRouteElementI> allRoutes, DRouteElementI currentRoute, DRouteElementI previousRoute) {
		List<DRouteElementI> connectedRoutes = RouteHelper.getLinkedTo(allRoutes, currentRoute);
		if ( currentRoute instanceof DConditionElement ) {
			for (DRouteElementI nextRoute : connectedRoutes) {
				RouteHelper.linkRoutes(previousRoute, (DRouteElement) nextRoute);
				
				// Mark the pass route condition
				if ( nextRoute instanceof DServiceDefinition && nextRoute.getDestinationId().equals(((DConditionElement) currentRoute).getPassRouteId())) {
					((DServiceDefinition)nextRoute).setCondition(((DConditionElement) currentRoute).getCondition());
				}
			}
		} else {
			if ( currentRoute instanceof DRouteElement )
				newRoutes.add((DRouteElement) currentRoute);
		}
		
		for (DRouteElementI element : connectedRoutes) {
			if ( element instanceof DRouteElement )
				pack(newRoutes, allRoutes, (DRouteElement) element, currentRoute);
		}
	}
	
	private static void updateFailNode(List<DRouteElementI> routes, DConditionElement condition, DRouteElementI rootRoute) {
		for (DRouteElementI t : RouteHelper.getLinkedTo(routes, rootRoute)) {
			if ( t instanceof DConditionElement ) {
				continue;
			}

			condition.setFailRouteId(t.getDestinationId());
			
			if ( t instanceof DServiceDefinition ) {
				((DServiceDefinition)t).setSource(condition.getDestination());
				((DServiceDefinition)t).setSourceId(condition.getDestinationId());
			}
		}
	}

	/**
	 * Instantiate new condition. Add it to the ServiceChain routes.
	 */
	public static DConditionElement newCondition(DServiceChain serviceChain) {
		DConditionElement condition = new DConditionElement();
		fixCondition(condition);
		serviceChain.addRoute(condition);
		return condition;
	}

	/**
	 * Instantiate new service definition. Add it to the ServiceChain routes.
	 */
	public static DServiceDefinition newServiceDefinition(DServiceChain serviceChain) {
		DServiceDefinition sDef = new DServiceDefinition();
		sDef.setDesination("ServiceDefinition");
		fixServiceDefinition(sDef);
		serviceChain.addRoute(sDef);
		return sDef;
	}
	
	/**
	 * Extra logic for when a service definition is saved. Currently unused.
	 */
	public static void saveServiceChain(DServiceChain serviceChain) {
		//
	}
	
	public static String getDefaultServiceChainColor() {
		return "#32A03CFF";
	}
	
	public static double getDefaultServiceChainElementX() {
		return 1978;
	}
	
	public static double getDefaultServiceChainElementY() {
		return 1800;
	}
	
	public static double getDefaultServiceChainElementWidth() {
		return 140.0;
	}
	
	public static double getDefaultServiceChainElementHeight() {
		return 80.0;
	}

	public static String getDefaultServiceChainName() {
		return "New Service Chain";
	}

	public static String getDefaultServiceChainHandlerId() {
		return "Entry Point";
	}

	public static String getDefaultServiceChainLastInput() {
		return "{}";
	}
	
	public static String getDefaultServiceDefinitionColor() {
		return "#008B8BFF";
	}
	
	public static double getDefaultServiceDefinitionX() {
		return 1940.0;
	}
	
	public static double getDefaultServiceDefinitionY() {
		return 1900.0;
	}
	
	public static double getDefaultServiceDefinitionWidth() {
		return 220.0;
	}
	
	public static double getDefaultServiceDefinitionHeight() {
		return 60.0;
	}
	
	public static double getDefaultConditionElementWidth() {
		return 100;
	}
	
	public static double getDefaultConditionElementHeight() {
		return 100;
	}
	
	public static String getDefaultConditionElementColor() {
		return "#FDDA0D";
	}
}

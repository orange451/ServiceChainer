package dev.anarchy.translate.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import dev.anarchy.common.DConditionElement;
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
		condition.setName("Condition");
		condition.setColor(ServiceChainHelper.getDefaultConditionElementColor());
		condition.setSize(getDefaultConditionElementWidth(), getDefaultConditionElementHeight());
		condition.setPosition(getDefaultServiceDefinitionX(), getDefaultServiceDefinitionY());
	}


	/**
	 * Instantiate new condition node
	 */
	public static DConditionElement newCondition(DServiceChain serviceChain) {
		DConditionElement condition = new DConditionElement();
		fixCondition(condition);
		serviceChain.addRoute(condition);
		return condition;
	}
	
	public static void saveServiceChain(DServiceChain serviceChain) {
		for (DRouteElementI elements : serviceChain.getRoutesUnmodifyable()) {
			//
		}
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

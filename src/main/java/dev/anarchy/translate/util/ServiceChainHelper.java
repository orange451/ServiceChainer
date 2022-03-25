package dev.anarchy.translate.util;

import org.apache.commons.lang3.StringUtils;

import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;

public class ServiceChainHelper {
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
		
		// Layout children
		int element = 0;
		for (DRouteElementI route : serviceChain.getRoutesUnmodifyable()) {
			double spacing = getDefaultServiceDefinitionY() - getDefaultServiceChainElementY();
			route.setPosition(getDefaultServiceDefinitionX(), getDefaultServiceDefinitionY() + (spacing*element));
			route.setSize(getDefaultServiceDefinitionWidth(), getDefaultServiceDefinitionHeight());
			route.setColor(getDefaultServiceDefinitionColor());
			route.setName(route.getDestination());
			element = element + 1;
		}
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
	
	public static String getDefaultConditionColor() {
		return "#FDDA0D";
	}
}

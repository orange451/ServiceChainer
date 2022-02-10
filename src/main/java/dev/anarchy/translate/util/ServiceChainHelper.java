package dev.anarchy.translate.util;

import dev.anarchy.common.DRouteElementI;
import dev.anarchy.common.DServiceChain;

public class ServiceChainHelper {
	/**
	 * Attempt to re-set all the required metadata to display a service chain and its subsequent routes.
	 */
	public static void fixServiceChain(DServiceChain serviceChain) {
		serviceChain.setPosition(getDefaultServiceChainElementX(), getDefaultServiceChainElementY());
		serviceChain.setName(getDefaultServiceChainName());
		serviceChain.setColor(getDefaultServiceChainColor());
		serviceChain.setHandlerId(getDefaultServiceChainHandlerId());
		serviceChain.setLastInput(getDefaultServiceChainLastInput());
		serviceChain.setSize(getDefaultServiceChainElementWidth(), getDefaultServiceChainElementHeight());
		
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
	
	public static String getDefaultServiceChainColor() {
		return "#32A03CFF";
	}
	
	public static double getDefaultServiceChainElementX() {
		return 960.0;
	}
	
	public static double getDefaultServiceChainElementY() {
		return 880.0;
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
		return 920.0;
	}
	
	public static double getDefaultServiceDefinitionY() {
		return 1020.0;
	}
	
	public static double getDefaultServiceDefinitionWidth() {
		return 220.0;
	}
	
	public static double getDefaultServiceDefinitionHeight() {
		return 60.0;
	}
}
